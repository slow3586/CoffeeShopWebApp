package com.slow3586.drinkshop.mainservice.service;

import com.slow3586.drinkshop.api.mainservice.OrderRequest;
import com.slow3586.drinkshop.api.mainservice.entity.Order;
import com.slow3586.drinkshop.api.mainservice.entity.OrderItem;
import com.slow3586.drinkshop.api.mainservice.entity.Payment;
import com.slow3586.drinkshop.mainservice.repository.OrderItemRepository;
import com.slow3586.drinkshop.mainservice.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class OrderService {
    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    TransactionTemplate transactionTemplate;
    KafkaTemplate<UUID, Object> kafkaTemplate;
    CustomerService customerService;
    ShopService shopService;
    ProductService productService;

    @KafkaListener(topics = "order.request")
    @Transactional(transactionManager = "transactionManager")
    public void createOrder(OrderRequest orderRequest) {
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

        kafkaTemplate.send("order.created",
            order.getId(),
            order);
    }

    @Transactional(transactionManager = "transactionManager")
    public void cancelOrder(UUID orderId) {
        Order entity = orderRepository.findById(orderId).get();
        entity.setStatus("CANCELLED");
        orderRepository.save(entity);
        kafkaTemplate.send("order.cancelled", orderId);
    }

    @Transactional(transactionManager = "transactionManager")
    public void completeOrder(UUID orderId) {
        Order entity = orderRepository.findById(orderId).get();
        if (!entity.getStatus().equals("IN_PROGRESS")) {
            throw new IllegalStateException("Order status is not in IN_PROGRESS");
        }
        entity.setStatus("COMPLETED");
        orderRepository.save(entity);
        kafkaTemplate.send("order.completed", orderId);
    }

    @KafkaListener(topics = "order.payment")
    @Transactional(transactionManager = "transactionManager")
    public void processOrderAwaitingPayment(Order order) {
        Order entity = orderRepository.findById(order.getId()).get();
        entity.setStatus("AWAITING_PAYMENT");
        orderRepository.save(entity);
    }

    @KafkaListener(topics = "payment.paid")
    @Transactional(transactionManager = "transactionManager")
    public void processPaymentPaid(Payment payment) {
        Order order = orderRepository.findById(payment.getOrderId())
            .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus("PAID");
        orderRepository.save(order);
    }
}
