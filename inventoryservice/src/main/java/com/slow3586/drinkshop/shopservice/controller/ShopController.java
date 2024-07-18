package com.slow3586.drinkshop.shopservice.controller;


import com.slow3586.drinkshop.api.mainservice.entity.Shop;
import com.slow3586.drinkshop.shopservice.repository.ShopRepository;
import com.slow3586.drinkshop.shopservice.service.InventoryService;
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
    InventoryService inventoryService;

    @GetMapping("findById/{id}")
    public Shop findById(@PathVariable UUID id) {
        return shopRepository.findById(id).get();
    }

    @GetMapping("findAll")
    @Secured({"SYSTEM", "CASHIER", "ADMIN"})
    public List<Shop> findAll() {
        return shopRepository.findAll().stream()
            .map(shop -> shop.setShopInventoryList(
                inventoryService.findAllByShopId(shop.getId())))
            .toList();
    }
}
