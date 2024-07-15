package com.slow3586.drinkshop.mainservice.service;


import com.slow3586.drinkshop.api.mainservice.entity.Shop;
import com.slow3586.drinkshop.api.mainservice.entity.ShopInventory;
import com.slow3586.drinkshop.mainservice.repository.ShopInventoryRepository;
import com.slow3586.drinkshop.mainservice.repository.ShopRepository;
import io.vavr.collection.List;
import jakarta.ws.rs.QueryParam;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("api/shop")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class ShopService {
    ShopRepository shopRepository;
    ShopInventoryRepository shopInventoryRepository;

    @GetMapping("all")
    @Secured({"SYSTEM", "CASHIER", "ADMIN"})
    public List<Shop> all() {
        return shopRepository.findAll()
            .map(shop -> shop.setShopInventoryList(
                shopInventoryRepository.findAllByShopId(shop.getId())));
    }
}
