package com.slow3586.drinkshop.mainservice.productinventory;

import com.slow3586.drinkshop.api.ProductDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/productinventory")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ProductInventoryService {
    ProductInventoryRepository productInventoryRepository;
    ProductInventoryMapper productInventoryMapper;
}
