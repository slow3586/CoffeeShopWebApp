package com.slow3586.drinkshop.mainservice.service;

import com.slow3586.drinkshop.api.mainservice.dto.PaymentSystemUpdate;
import com.slow3586.drinkshop.api.mainservice.entity.Order;
import com.slow3586.drinkshop.api.mainservice.entity.Payment;
import com.slow3586.drinkshop.api.mainservice.topic.OrderTopics;
import com.slow3586.drinkshop.api.mainservice.topic.PaymentTopics;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class PaymentService {
    PaymentRepository paymentRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;
    StreamsBuilder streamsBuilder;

    @PostConstruct
    public void processOrder() {
        JsonSerde<Order> orderSerde = new JsonSerde<>(Order.class);

        streamsBuilder.table(OrderTopics.Transaction.CUSTOMER,
                Consumed.with(Serdes.UUID(), orderSerde))
            .join(streamsBuilder.table(OrderTopics.Transaction.INVENTORY,
                    Consumed.with(Serdes.UUID(), orderSerde)),
                (a, b) -> a.setOrderItemList(b.getOrderItemList()))
            .join(streamsBuilder.table(OrderTopics.Transaction.SHOP,
                    Consumed.with(Serdes.UUID(), orderSerde)),
                (a, b) -> a.setShop(b.getShop()))
            .toStream()
            .foreach((k, order) -> {
                try {
                    if (paymentRepository.existsByOrderId(order.getId())) {return;}

                    kafkaTemplate.executeInTransaction((operations) -> {
                        final int price = order.getOrderItemList().stream()
                            .mapToInt(i -> i.getQuantity() * i.getProduct().getPrice())
                            .sum();
                        final int payInPoints = (order.getCustomer() != null && order.isUsePoints())
                            ? Math.max(order.getCustomer().getPoints(), price)
                            : 0;
                        final int payInMoney = price - payInPoints;

                        order.setPayment(
                            paymentRepository.save(new Payment()
                                .setValue(payInMoney)
                                .setStatus("CREATED")
                                .setPoints(payInPoints)
                                .setOrderId(order.getId())));

                        operations.send(OrderTopics.Transaction.PAYMENT,
                            order.getId(),
                            order);

                        return true;
                    });
                } catch (Exception e) {
                    kafkaTemplate.executeInTransaction((operations) -> {
                        log.error("PaymentService#processOrder: {}", e.getMessage(), e);
                        operations.send(OrderTopics.Transaction.ERROR,
                            order.getId(),
                            order.setError(e.getMessage()));
                        return true;
                    });
                }
            });
    }

    @KafkaListener(topics = OrderTopics.Transaction.ERROR, groupId = "paymentservice")
    @Transactional(transactionManager = "transactionManager")
    public void orderError(Order order) {
        try {
            paymentRepository.findByOrderId(order.getId())
                .ifPresent(payment -> {
                    payment.setStatus("ORDER_CANCELLED");
                    paymentRepository.save(payment);
                });
        } catch (Exception e) {
            log.error("PaymentService#orderError: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = PaymentTopics.REQUEST_SYSTEM_RESPONSE,
        groupId = "paymentservice",
        errorHandler = "defaultKafkaListenerErrorHandler")
    public void processUpdate(PaymentSystemUpdate update) {
        Payment payment = paymentRepository.findByOrderId(update.getOrderId())
            .orElseThrow()
            .setCheckId(update.getCheckId())
            .setCheckReceivedAt(Instant.now())
            .setCheckNote("");

        kafkaTemplate.send(
            PaymentTopics.STATUS_PAID,
            payment.getOrderId(),
            payment);
    }

}
