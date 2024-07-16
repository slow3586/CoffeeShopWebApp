package com.slow3586.drinkshop.mainservice.service;

import com.slow3586.drinkshop.api.mainservice.OrderTopics;
import com.slow3586.drinkshop.api.mainservice.PaymentSystemUpdate;
import com.slow3586.drinkshop.api.mainservice.PaymentTopics;
import com.slow3586.drinkshop.api.mainservice.entity.Order;
import com.slow3586.drinkshop.api.mainservice.entity.Payment;
import com.slow3586.drinkshop.mainservice.repository.PaymentRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class PaymentService {
    PaymentRepository paymentRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;
    StreamsBuilder streamsBuilder;
    OrderService orderService;
    TransactionTemplate transactionTemplate;
    private final KafkaAdmin kafkaAdmin;

    @PostConstruct
    public void orderCreatedStream() {
        JsonSerde<Order> orderSerde = new JsonSerde<>(Order.class);
        streamsBuilder.table("order.transaction.customer",
                Consumed.with(Serdes.String(), orderSerde))
            .join(streamsBuilder.table("order.transaction.inventory",
                    Consumed.with(Serdes.String(), orderSerde)),
                (a, b) -> a.setOrderItemList(b.getOrderItemList()))
            .join(streamsBuilder.table("order.transaction.shop",
                    Consumed.with(Serdes.String(), orderSerde)),
                (a, b) -> a.setShop(b.getShop()))
            .toStream()
            .foreach((k, order) -> {
                try {
                    transactionTemplate.executeWithoutResult((status) -> {
                        final int price = order.getOrderItemList().stream()
                            .mapToInt(i -> i.getQuantity() * i.getProduct().getPrice())
                            .sum();
                        final int payInPoints = order.getCustomer() != null
                            && order.isUsePoints() ? Math.max(order.getCustomer().getPoints(), price) : 0;
                        final int payInMoney = price - payInPoints;

                        if (payInPoints > 0) {
                            order.setPaymentPoints(
                                paymentRepository.save(new Payment()
                                    .setValue(payInPoints)
                                    .setPaymentSystem("POINTS")
                                    .setStatus("CREATED")
                                    .setOrderId(order.getId())));
                        }
                        if (payInMoney > 0) {
                            order.setPaymentMoney(
                                paymentRepository.save(new Payment()
                                    .setValue(payInMoney)
                                    .setPaymentSystem("MONEY")
                                    .setStatus("CREATED")
                                    .setOrderId(order.getId())));
                        }

                        kafkaTemplate.send(OrderTopics.TRANSACTION_PAYMENT,
                            order.getId(),
                            order);
                    });
                } catch (Exception e) {
                    kafkaTemplate.send(OrderTopics.TRANSACTION_PAYMENT_ERROR,
                        order.getId(),
                        e.getMessage());
                }
            });
    }

    @KafkaListener(topics = OrderTopics.STATUS_ERROR)
    @Transactional(transactionManager = "transactionManager")
    public void orderCancelled(UUID orderId) {
        paymentRepository.findByOrderId(orderId)
            .forEach(payment -> {
                payment.setStatus("ORDER_CANCELLED");
                paymentRepository.save(payment);
            });
    }

    @KafkaListener(topics = PaymentTopics.REQUEST_SYSTEM_RESPONSE)
    public void processUpdate(PaymentSystemUpdate update) {
        Payment payment = paymentRepository.findById(update.getPaymentId())
            .get()
            .setCheckId("checkId")
            .setCheckReceivedAt(Instant.now())
            .setCheckNote("");

        kafkaTemplate.send(
            PaymentTopics.STATUS_PAID,
            payment.getId(),
            payment);
    }

}
