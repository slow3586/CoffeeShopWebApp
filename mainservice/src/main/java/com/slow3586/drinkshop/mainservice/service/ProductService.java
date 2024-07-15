package com.slow3586.drinkshop.mainservice.service;


import com.slow3586.drinkshop.api.mainservice.entity.Product;
import com.slow3586.drinkshop.api.mainservice.entity.ProductInventory;
import com.slow3586.drinkshop.api.mainservice.entity.ProductInventoryType;
import com.slow3586.drinkshop.api.mainservice.entity.ProductGroup;
import com.slow3586.drinkshop.mainservice.repository.ProductInventoryRepository;
import com.slow3586.drinkshop.mainservice.repository.ProductInventoryTypeRepository;
import com.slow3586.drinkshop.mainservice.repository.ProductRepository;
import com.slow3586.drinkshop.mainservice.repository.ProductGroupRepository;
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
@RequestMapping("api/product")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class ProductService {
    ProductRepository productRepository;
    ProductGroupRepository productGroupRepository;
    ProductInventoryRepository productInventoryRepository;
    ProductInventoryTypeRepository productInventoryTypeRepository;

    @GetMapping("/all")
    @Secured({"SYSTEM", "CASHIER", "ADMIN"})
    public List<Product> all() {
        return productRepository.findAll()
            .map(p -> p.setProductInventoryList(
                productInventoryRepository.findByProductId(p.getId())
                    .map(pi -> pi.setProductInventoryType(
                        productInventoryTypeRepository.findById(
                            pi.getProductInventoryTypeId()).get()))));
    }
}
