package com.slow3586.drinkshop.shopservice.service;


import com.slow3586.drinkshop.api.mainservice.entity.Order;
import com.slow3586.drinkshop.api.mainservice.entity.Shop;
import com.slow3586.drinkshop.api.mainservice.topic.OrderTopics;
import com.slow3586.drinkshop.shopservice.repository.ShopRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class ShopService {
    ShopRepository shopRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @KafkaListener(topics = OrderTopics.Transaction.CREATED, groupId = "shopservice", errorHandler = "orderTransactionListenerErrorHandler")
    public void processOrder(Order order) {
        kafkaTemplate.send(OrderTopics.Transaction.SHOP,
            order.getId(),
            order.setShop(Optional.ofNullable(order.getShopId())
                .flatMap(c -> shopRepository.findById(order.getShopId()))
                .orElseThrow()));
    }

    public Shop findById(UUID shopId) {
        return shopRepository.findById(shopId).get();
    }
}
