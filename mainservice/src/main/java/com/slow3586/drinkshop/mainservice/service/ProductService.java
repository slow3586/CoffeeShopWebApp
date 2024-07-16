package com.slow3586.drinkshop.mainservice.service;


import com.slow3586.drinkshop.api.mainservice.entity.Order;
import com.slow3586.drinkshop.api.mainservice.entity.Product;
import com.slow3586.drinkshop.api.mainservice.topic.OrderTopics;
import com.slow3586.drinkshop.mainservice.repository.ProductGroupRepository;
import com.slow3586.drinkshop.mainservice.repository.ProductInventoryRepository;
import com.slow3586.drinkshop.mainservice.repository.ProductInventoryTypeRepository;
import com.slow3586.drinkshop.mainservice.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

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
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @KafkaListener(topics = OrderTopics.TRANSACTION_CREATED)
    public void processOrder(Order order) {
        try {
            kafkaTemplate.send(OrderTopics.TRANSACTION_PRODUCT,
                order.getId(),
                order.getOrderItemList().stream().map(orderItem ->
                    orderItem.setProduct(productRepository.findById(orderItem.getProductId()).get()
                        .setProductInventoryList(productInventoryRepository.findByProductId(orderItem.getProductId())))));
        } catch (Exception e) {
            kafkaTemplate.send(OrderTopics.TRANSACTION_PRODUCT_ERROR,
                order.getId(),
                e.getMessage());
        }
    }

    public Product findById(UUID productId) {
        return productRepository.findById(productId).get();
    }
}
