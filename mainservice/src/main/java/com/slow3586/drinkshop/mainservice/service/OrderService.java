package com.slow3586.drinkshop.mainservice.service;

import com.slow3586.drinkshop.api.mainservice.OrderRequest;
import com.slow3586.drinkshop.api.mainservice.OrderTransaction;
import com.slow3586.drinkshop.api.mainservice.entity.CustomerOrder;
import com.slow3586.drinkshop.api.mainservice.entity.Product;
import com.slow3586.drinkshop.api.mainservice.entity.ProductInventory;
import com.slow3586.drinkshop.api.mainservice.entity.ShopInventory;
import com.slow3586.drinkshop.mainservice.repository.CustomerOrderRepository;
import com.slow3586.drinkshop.mainservice.repository.ProductInventoryRepository;
import com.slow3586.drinkshop.mainservice.repository.ProductRepository;
import com.slow3586.drinkshop.mainservice.repository.ProductTypeRepository;
import com.slow3586.drinkshop.mainservice.repository.ShopInventoryRepository;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.streams.StreamsBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@RestController
@RequestMapping("api/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class OrderService {
    CustomerOrderRepository customerOrderRepository;
    ProductRepository productRepository;
    ProductTypeRepository productTypeRepository;
    ProductInventoryRepository productInventoryRepository;
    ShopInventoryRepository shopInventoryRepository;
    StreamsBuilder streamsBuilder;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @Transactional(transactionManager = "kafkaTransactionManager")
    @PostMapping("create_order")
    public void createOrder(@RequestBody @NonNull OrderRequest orderRequest) {
        if (!productRepository.existsById(orderRequest.getProductId())) {
            throw new IllegalArgumentException();
        }

        List<ShopInventory> shopInventoryList = productInventoryRepository
            .findByProductId(orderRequest.getProductId())
            .stream()
            .map(i -> shopInventoryRepository.findByShopIdAndInventoryTypeId(
                orderRequest.getShopId(),
                i.getInventoryId()))
            .toList();
        if (!shopInventoryList.stream()
            .filter(i1 -> i1.getQuantity() - i1.getReserved() < 0)
            .toList()
            .isEmpty()
        ) {
            throw new IllegalStateException();
        }

        shopInventoryList.stream()
            .map(i -> i.setReserved(i.getReserved()
                + productInventoryRepository.findByProductId(orderRequest.getProductId())
                .stream()
                .filter(p -> p.getInventoryId().equals(i.getId()))
                .findFirst()
                .get()
                .getQuantity()))
            .forEach(shopInventoryRepository::save);
    }
}
