package com.slow3586.drinkshop.mainservice.service;

import com.slow3586.drinkshop.api.mainservice.dto.OrderRequest;
import com.slow3586.drinkshop.api.mainservice.entity.Order;
import com.slow3586.drinkshop.api.mainservice.entity.OrderItem;
import com.slow3586.drinkshop.api.mainservice.entity.Payment;
import com.slow3586.drinkshop.api.mainservice.topic.OrderTopics;
import com.slow3586.drinkshop.api.mainservice.topic.PaymentTopics;
import com.slow3586.drinkshop.mainservice.repository.OrderItemRepository;
import com.slow3586.drinkshop.mainservice.repository.OrderRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Suppressed;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class OrderService {
    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;
    CustomerService customerService;
    ShopService shopService;
    ProductService productService;
    StreamsBuilder streamsBuilder;

    @KafkaListener(topics = OrderTopics.REQUEST_CREATE, groupId = "orderservice", errorHandler = "kafkaListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    @SendTo(OrderTopics.REQUEST_CREATE_RESPONSE)
    public UUID createOrder(OrderRequest orderRequest) {
        final Order order =
            orderRepository.save(
                new Order()
                    .setCreatedAt(Instant.now())
                    .setCustomerId(orderRequest.getCustomerId())
                    .setShopId(orderRequest.getShopId())
                    .setStatus("CREATED"));

        order.setOrderItemList(
            orderItemRepository.saveAll(
                orderRequest.getOrderRequestItemList().stream()
                    .map(productQuantity -> new OrderItem()
                        .setOrderId(order.getId())
                        .setProductId(productQuantity.getProductId())
                        .setQuantity(productQuantity.getQuantity()))
                    .toList()));

        kafkaTemplate.send(OrderTopics.TRANSACTION_CREATED,
            order.getId(),
            order);

        return order.getId();
    }

    @KafkaListener(topics = OrderTopics.REQUEST_CANCEL, groupId = "orderservice", errorHandler = "kafkaListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    @SendTo(OrderTopics.REQUEST_CANCEL_RESPONSE)
    public UUID cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId).get();
        if (!order.getStatus().equals("AWAITING_PAYMENT")) {
            throw new IllegalStateException("Order status is not in IN_PROGRESS");
        }
        order.setStatus("CANCELLED");
        order = orderRepository.save(order);
        kafkaTemplate.send(OrderTopics.STATUS_COMPLETED, order.getId(), order);
        return order.getId();
    }

    @KafkaListener(topics = OrderTopics.REQUEST_COMPLETED, groupId = "orderservice", errorHandler = "kafkaListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    @SendTo(OrderTopics.REQUEST_COMPLETED_RESPONSE)
    public UUID completeOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId).get();
        if (!order.getStatus().equals("PAID")) {
            throw new IllegalStateException("Order status is not in PAID");
        }
        order.setStatus("COMPLETED");
        order = orderRepository.save(order);
        kafkaTemplate.send(OrderTopics.STATUS_COMPLETED, order.getId(), order);

        return order.getId();
    }

    @PostConstruct
    public void orderStreams() {
        JsonSerde<Order> orderSerde = new JsonSerde<>(Order.class);
        JsonSerde<Payment> paymentSerde = new JsonSerde<>(Payment.class);

        KTable<UUID, Order> orderCreatedTable = streamsBuilder.table(
            OrderTopics.TRANSACTION_CREATED,
            Consumed.with(Serdes.UUID(), orderSerde));
        KTable<UUID, Order> orderPaymentTable = streamsBuilder.table(
            OrderTopics.TRANSACTION_PAYMENT,
            Consumed.with(Serdes.UUID(), orderSerde));
        KTable<UUID, Order> orderPaidTable = streamsBuilder.table(
            OrderTopics.TRANSACTION_PAID,
            Consumed.with(Serdes.UUID(), orderSerde));
        KTable<UUID, Order> orderCompleteTable = streamsBuilder.table(
            OrderTopics.TRANSACTION_COMPLETED,
            Consumed.with(Serdes.UUID(), orderSerde));
        KTable<UUID, Order> orderCancelledStatus = streamsBuilder.table(
            OrderTopics.STATUS_CANCELLED,
            Consumed.with(Serdes.UUID(), orderSerde));
        KTable<UUID, Order> orderCompletedStatus = streamsBuilder.table(
            OrderTopics.STATUS_COMPLETED,
            Consumed.with(Serdes.UUID(), orderSerde));
        KTable<UUID, Payment> paidPaymentTable = streamsBuilder.table(PaymentTopics.STATUS_PAID,
            Consumed.with(Serdes.UUID(), paymentSerde));

        // CREATE TIMEOUT
        orderCreatedTable
            .suppress(Suppressed.untilTimeLimit(Duration.ofSeconds(10), null))
            .leftJoin(orderPaymentTable, (a, b) -> b)
            .filter((k, v) -> v == null)
            .mapValues(v -> v.setError("TIMEOUT_CREATE"))
            .toStream()
            .to(OrderTopics.TRANSACTION_ERROR);

        // PAYMENT RECEIVED
        orderPaymentTable
            .join(paidPaymentTable, Order::setPayment)
            .toStream()
            .to(OrderTopics.TRANSACTION_PAID);

        // PAYMENT TIMEOUT
        orderPaymentTable
            .suppress(Suppressed.untilTimeLimit(Duration.ofSeconds(60), null))
            .leftJoin(paidPaymentTable, Order::setPayment)
            .filter((k, v) -> v.getPayment() == null)
            .mapValues(v -> v.setError("TIMEOUT_PAYMENT"))
            .toStream()
            .to(OrderTopics.TRANSACTION_ERROR);

        // COMPLETE RECEIVED
        orderPaymentTable
            .join(orderCompletedStatus, (a, b) -> a)
            .toStream()
            .to(OrderTopics.TRANSACTION_COMPLETED);

        // COMPLETE TIMEOUT
        orderPaidTable
            .suppress(Suppressed.untilTimeLimit(Duration.ofMinutes(30), null))
            .leftJoin(orderCompletedStatus, (a, b) -> a.setCompletedAt(Instant.now()))
            .filter((k, v) -> v.getCompletedAt() == null)
            .mapValues(v -> v.setError("TIMEOUT_COMPLETED"))
            .toStream()
            .to(OrderTopics.TRANSACTION_ERROR);
    }

    @KafkaListener(topics = OrderTopics.TRANSACTION_PUBLISH, groupId = "orderservice")
    @Transactional(transactionManager = "transactionManager")
    public void processOrderAwaitingPayment(Order order) {
        try {
            orderRepository.save(order.setStatus("AWAITING_PAYMENT"));
        } catch (Exception e) {
            kafkaTemplate.send(
                OrderTopics.TRANSACTION_ERROR,
                order.getId(),
                order.setError(e.getMessage()));
        }
    }

    @KafkaListener(topics = OrderTopics.TRANSACTION_PAID, groupId = "orderservice")
    @Transactional(transactionManager = "transactionManager")
    public void processOrderPaid(Order order) {
        try {
            orderRepository.save(order.setStatus("PAID"));
        } catch (Exception e) {
            kafkaTemplate.send(
                OrderTopics.TRANSACTION_ERROR,
                order.getId(),
                e.getMessage());
        }
    }

    @KafkaListener(topics = {OrderTopics.TRANSACTION_ERROR}, groupId = "orderservice")
    @Transactional(transactionManager = "transactionManager")
    public void processOrderError(Order order) {
        try {
            orderRepository.save(order.setStatus("ERROR"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public List<Order> findAllActiveByShopId(UUID shopId) {
        return orderRepository.findAllActiveByShopId(shopId).stream()
            .map(order -> order
                .setCustomer(order.getCustomerId() != null
                    ? customerService.findById(order.getCustomerId())
                    : null)
                .setShop(shopService.findById(order.getShopId()))
                .setOrderItemList(
                    orderItemRepository.findAllByOrderId(order.getId()).stream()
                        .map(i -> i.setProduct(productService.findById(i.getProductId())))
                        .toList()))
            .toList();
    }
}
