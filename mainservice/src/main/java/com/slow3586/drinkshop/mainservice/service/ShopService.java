package com.slow3586.drinkshop.mainservice.service;


import com.slow3586.drinkshop.api.mainservice.entity.Order;
import com.slow3586.drinkshop.api.mainservice.entity.Shop;
import com.slow3586.drinkshop.api.mainservice.topic.OrderTopics;
import com.slow3586.drinkshop.api.mainservice.topic.PromoTopics;
import com.slow3586.drinkshop.mainservice.repository.ShopRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/shop")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class ShopService {
    ShopRepository shopRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @KafkaListener(topics =  OrderTopics.TRANSACTION_CREATED)
    public void processOrder(Order order) {
        try {
            kafkaTemplate.send( OrderTopics.TRANSACTION_SHOP,
                order.getId(),
                order.setShop(Optional.ofNullable(order.getShopId())
                    .flatMap(c -> shopRepository.findById(order.getShopId()))
                    .get()));
        } catch (Exception e) {
            log.error("ShopService#processOrder: {}", e.getMessage(), e);
            kafkaTemplate.send(
                OrderTopics.TRANSACTION_SHOP_ERROR,
                order.getId(),
                e.getMessage());
        }
    }

    public Shop findById(UUID shopId) {
        return shopRepository.findById(shopId).get();
    }
}
