package com.slow3586.drinkshop.mainservice.service;


import com.slow3586.drinkshop.api.mainservice.entity.Order;
import com.slow3586.drinkshop.api.mainservice.entity.ShopInventory;
import com.slow3586.drinkshop.api.mainservice.topic.OrderTopics;
import com.slow3586.drinkshop.mainservice.repository.ShopInventoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/customer")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class InventoryService {
    ShopInventoryRepository shopInventoryRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @KafkaListener(topics = OrderTopics.TRANSACTION_PRODUCT)
    public void processOrder(Order order) {
        try {
            kafkaTemplate.send(OrderTopics.TRANSACTION_INVENTORY,
                order.getId(),
                order.getOrderItemList().stream().map(orderItem ->
                    orderItem.getProduct().getProductInventoryList().stream().map(productInventory -> {
                        final ShopInventory shopInventory =
                            shopInventoryRepository.findByShopIdAndProductInventoryTypeId(
                                    order.getShopId(),
                                    productInventory.getProductInventoryTypeId())
                                .orElseThrow(() -> new IllegalArgumentException("Shop Inventory not found for ShopId: "
                                    + order.getShopId() + " and InventoryId: "
                                    + productInventory.getProductInventoryTypeId()));

                        final int requiredQuantity = productInventory.getQuantity() * orderItem.getQuantity();
                        final int availableQuantity = shopInventory.getQuantity() - shopInventory.getReserved();

                        if (availableQuantity < requiredQuantity) {
                            throw new IllegalStateException("Not enough product inventory in shop for order item: "
                                + orderItem.getId());
                        }

                        shopInventory.setReserved(shopInventory.getReserved() + requiredQuantity);
                        return productInventory.setShopInventory(shopInventoryRepository.save(shopInventory));
                    })));
        } catch (Exception e) {
            log.error("InventoryService#processOrder: {}", e.getMessage(), e);
            kafkaTemplate.send(
                OrderTopics.TRANSACTION_INVENTORY_ERROR,
                order.getId(),
                e.getMessage());
        }
    }

    @KafkaListener(topics = {OrderTopics.STATUS_ERROR, OrderTopics.STATUS_CANCELLED})
    public void revertOrder(Order order) {
        order.getOrderItemList()
            .forEach(item -> item.getProduct().getProductInventoryList()
                .forEach(productInventory -> {
                    ShopInventory entity = shopInventoryRepository.findByShopIdAndProductInventoryTypeId(
                        order.getShopId(),
                        productInventory.getProductInventoryTypeId()
                    ).get();
                    shopInventoryRepository.save(
                        entity.setReserved(entity.getReserved() - productInventory.getQuantity() * item.getQuantity()));
                }));
    }

    public List<ShopInventory> findAllByShopId(UUID id) {
        return shopInventoryRepository.findAllByShopId(id);
    }
}
