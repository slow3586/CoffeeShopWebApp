package com.slow3586.drinkshop.mainservice.controller;


import com.slow3586.drinkshop.api.mainservice.entity.Shop;
import com.slow3586.drinkshop.mainservice.repository.ShopRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/shop")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class ShopController {
    ShopRepository shopRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @GetMapping("findById/{id}")
    public Shop findById(@PathVariable UUID id) {
        return shopRepository.findById(id).get();
    }

    @GetMapping("all")
    @Secured({"SYSTEM", "CASHIER", "ADMIN"})
    public List<Shop> all() {
        return shopRepository.findAll()
            .map(shop -> shop.setShopInventoryList(
                shopInventoryRepository.findAllByShopId(shop.getId())));
    }
}
