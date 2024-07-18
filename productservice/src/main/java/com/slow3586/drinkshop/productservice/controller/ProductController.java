package com.slow3586.drinkshop.productservice.controller;


import com.slow3586.drinkshop.api.mainservice.entity.Product;
import com.slow3586.drinkshop.productservice.repository.ProductGroupRepository;
import com.slow3586.drinkshop.productservice.repository.ProductInventoryRepository;
import com.slow3586.drinkshop.productservice.repository.ProductInventoryTypeRepository;
import com.slow3586.drinkshop.productservice.repository.ProductRepository;
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
@RequestMapping("api/product")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class ProductController {
    ProductRepository productRepository;
    ProductGroupRepository productGroupRepository;
    ProductInventoryRepository productInventoryRepository;
    ProductInventoryTypeRepository productInventoryTypeRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @GetMapping("findById/{id}")
    public Product findById(@PathVariable UUID id) {
        return productRepository.findById(id).get();
    }

    @GetMapping("findAll")
    @Secured({"SYSTEM", "CASHIER", "ADMIN"})
    public List<Product> findAll() {
        return productRepository.findAll().stream()
            .map(p -> p.setProductInventoryList(
                productInventoryRepository.findByProductId(p.getId()).stream()
                    .map(pi -> pi.setProductInventoryType(
                        productInventoryTypeRepository.findById(
                            pi.getProductInventoryTypeId()).get()))
                    .toList()))
            .toList();
    }
}
