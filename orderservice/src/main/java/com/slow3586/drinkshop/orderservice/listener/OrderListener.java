package com.slow3586.drinkshop.orderservice.listener;

import com.slow3586.drinkshop.api.mainservice.dto.OrderRequest;
import com.slow3586.drinkshop.api.mainservice.entity.Order;
import com.slow3586.drinkshop.api.mainservice.entity.OrderItem;
import com.slow3586.drinkshop.api.mainservice.topic.OrderTopics;
import com.slow3586.drinkshop.orderservice.repository.OrderItemRepository;
import com.slow3586.drinkshop.orderservice.repository.OrderRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class OrderListener {
    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @KafkaListener(topics = OrderTopics.Request.REQUEST_CREATE,
        errorHandler = "defaultKafkaListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    @SendTo(OrderTopics.Request.REQUEST_CREATE_RESPONSE)
    public UUID createOrder(@Valid OrderRequest orderRequest) {
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

        kafkaTemplate.send(OrderTopics.Transaction.CREATED,
            order.getId(),
            order);

        return order.getId();
    }

    @KafkaListener(topics = OrderTopics.Request.REQUEST_CANCEL,
        errorHandler = "defaultKafkaListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    @SendTo(OrderTopics.Request.REQUEST_CANCEL_RESPONSE)
    public UUID cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order with ID " + orderId + " not found"));
        if (!order.getStatus().equals("AWAITING_PAYMENT")) {
            throw new IllegalStateException("Order status is not in AWAITING_PAYMENT");
        }
        order = orderRepository.save(order.setStatus("CANCELLED"));
        kafkaTemplate.send(OrderTopics.Status.STATUS_COMPLETED,
            order.getId(),
            order);

        return order.getId();
    }

    @KafkaListener(topics = OrderTopics.Request.REQUEST_COMPLETED,
        errorHandler = "defaultKafkaListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    @SendTo(OrderTopics.Request.REQUEST_COMPLETED_RESPONSE)
    public UUID completeOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
            () -> new IllegalArgumentException("Order with id " + orderId + " doesn't exist."));
        if (!order.getStatus().equals("PAID")) {
            throw new IllegalStateException("Order status is not in PAID");
        }
        order = orderRepository.save(order.setStatus("COMPLETED"));
        kafkaTemplate.send(OrderTopics.Status.STATUS_COMPLETED,
            order.getId(),
            order);

        return order.getId();
    }

    @KafkaListener(topics = OrderTopics.Transaction.PUBLISH,
        errorHandler = "orderTransactionListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    public void processOrderAwaitingPayment(Order order) {
        orderRepository.save(order.setStatus("AWAITING_PAYMENT"));
    }

    @KafkaListener(topics = OrderTopics.Transaction.PAID,
        errorHandler = "orderTransactionListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    public void processOrderPaid(Order order) {
        orderRepository.save(order.setStatus("PAID"));
    }

    @KafkaListener(topics = {OrderTopics.Transaction.ERROR},
        errorHandler = "loggingKafkaListenerErrorHandler")
    @Transactional(transactionManager = "transactionManager")
    public void processOrderError(Order order) {
        try {
            orderRepository.save(order.setStatus("ERROR"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}