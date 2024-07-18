package com.slow3586.drinkshop.orderservice.service;

import com.slow3586.drinkshop.api.mainservice.client.CustomerClient;
import com.slow3586.drinkshop.api.mainservice.client.ProductClient;
import com.slow3586.drinkshop.api.mainservice.client.ShopClient;
import com.slow3586.drinkshop.api.mainservice.entity.Order;
import com.slow3586.drinkshop.api.mainservice.topic.OrderTopics;
import com.slow3586.drinkshop.orderservice.repository.OrderItemRepository;
import com.slow3586.drinkshop.orderservice.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class OrderService {
    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    CustomerClient customerClient;
    ShopClient shopClient;
    ProductClient productClient;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    public List<Order> findAllActiveByShopId(UUID shopId) {
        return orderRepository.findAllActiveByShopId(shopId).stream()
            .map(order -> order
                .setCustomer(order.getCustomerId() != null
                    ? customerClient.findById(order.getCustomerId())
                    : null)
                .setShop(shopClient.findById(order.getShopId()))
                .setOrderItemList(
                    orderItemRepository.findAllByOrderId(order.getId()).stream()
                        .map(orderItem -> orderItem.setProduct(
                            productClient.findById(orderItem.getProductId())))
                        .toList()))
            .toList();
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Async
    @Scheduled(cron = "* */1 * * * *")
    public void verify() {
        orderRepository.findBadOrders()
            .forEach(order ->
                kafkaTemplate.executeInTransaction((operations) ->
                    operations.send(OrderTopics.Transaction.ERROR,
                        order.getId(),
                        order.setError("TIMEOUT"))));
    }
}
