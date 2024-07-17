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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class ProductService {
    ProductRepository productRepository;
    ProductGroupRepository productGroupRepository;
    ProductInventoryRepository productInventoryRepository;
    ProductInventoryTypeRepository productInventoryTypeRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @KafkaListener(topics = OrderTopics.Transaction.CREATED, groupId = "productservice", errorHandler = "orderTransactionListenerErrorHandler")
    @Transactional(transactionManager = "kafkaTransactionManager")
    public void processOrder(Order order) {
        kafkaTemplate.send(OrderTopics.Transaction.PRODUCT,
            order.getId(),
            order.setOrderItemList(order.getOrderItemList().stream().map(orderItem ->
                    orderItem.setProduct(productRepository.findById(orderItem.getProductId()).get()
                        .setProductInventoryList(productInventoryRepository.findByProductId(orderItem.getProductId()))))
                .toList()));
    }

    public Product findById(UUID productId) {
        return productRepository.findById(productId).get();
    }
}
