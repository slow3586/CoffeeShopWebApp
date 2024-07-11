package com.slow3586.drinkshop.mainservice.productinventory;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/productinventory")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ProductInventoryService {
    ProductInventoryRepository productInventoryRepository;
    ProductInventoryMapper productInventoryMapper;
}
