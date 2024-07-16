package com.slow3586.drinkshop.mainservice.service;

import com.slow3586.drinkshop.api.mainservice.dto.OrderRequest;
import com.slow3586.drinkshop.api.mainservice.entity.Order;
import com.slow3586.drinkshop.api.mainservice.entity.OrderItem;
import com.slow3586.drinkshop.api.mainservice.entity.Payment;
import com.slow3586.drinkshop.api.mainservice.topic.OrderTopics;
import com.slow3586.drinkshop.api.mainservice.topic.PaymentTopics;
import com.slow3586.drinkshop.mainservice.repository.OrderItemRepository;
import com.slow3586.drinkshop.mainservice.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

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

    @KafkaListener(topics = OrderTopics.REQUEST_CREATE)
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

    @KafkaListener(topics = OrderTopics.REQUEST_CANCEL)
    @Transactional(transactionManager = "transactionManager")
    @SendTo(OrderTopics.REQUEST_CANCEL_RESPONSE)
    public UUID cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId).get();
        order.setStatus("CANCELLED");
        orderRepository.save(order);
        kafkaTemplate.send(OrderTopics.STATUS_CANCELLED, orderId);
        return order.getId();
    }

    @KafkaListener(topics = OrderTopics.REQUEST_COMPLETE)
    @Transactional(transactionManager = "transactionManager")
    @SendTo(OrderTopics.REQUEST_COMPLETE_RESPONSE)
    public UUID completeOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId).get();
        if (!order.getStatus().equals("IN_PROGRESS")) {
            throw new IllegalStateException("Order status is not in IN_PROGRESS");
        }
        order.setStatus("COMPLETED");
        orderRepository.save(order);
        kafkaTemplate.send(OrderTopics.STATUS_COMPLETED, orderId);

        return order.getId();
    }

    @KafkaListener(topics = OrderTopics.TRANSACTION_PAYMENT)
    @Transactional(transactionManager = "transactionManager")
    public void processOrderAwaitingPayment(Order order) {
        orderRepository.save(
            orderRepository.findById(order.getId()).get()
                .setStatus("AWAITING_PAYMENT"));

        kafkaTemplate.send(OrderTopics.STATUS_AWAITINGPAYMENT, order.getId());
    }

    @KafkaListener(topics = {
        OrderTopics.TRANSACTION_CUSTOMER_ERROR,
        OrderTopics.TRANSACTION_INVENTORY_ERROR,
        OrderTopics.TRANSACTION_SHOP_ERROR,
        OrderTopics.TRANSACTION_PRODUCT_ERROR,
        OrderTopics.TRANSACTION_PAYMENT_ERROR,
    })
    @Transactional(transactionManager = "transactionManager")
    public void processOrderError(Order order) {
        Order entity = orderRepository.findById(order.getId()).get();
        entity.setStatus("ERROR");
        orderRepository.save(entity);
        kafkaTemplate.send(OrderTopics.STATUS_ERROR, order.getId());
    }

    @KafkaListener(topics = PaymentTopics.STATUS_PAID)
    @Transactional(transactionManager = "transactionManager")
    public void processPaymentPaid(Payment payment) {
        orderRepository.save(
            orderRepository.findById(payment.getOrderId()).get()
                .setStatus("PAID"));

        kafkaTemplate.send(OrderTopics.STATUS_PAID, payment.getId());
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
