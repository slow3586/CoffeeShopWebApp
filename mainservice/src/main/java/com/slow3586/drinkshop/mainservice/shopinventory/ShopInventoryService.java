package com.slow3586.drinkshop.mainservice.shopinventory;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/shopinventory")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ShopInventoryService {
    ShopInventoryRepository shopInventoryRepository;
    ShopInventoryMapper shopInventoryMapper;
}
