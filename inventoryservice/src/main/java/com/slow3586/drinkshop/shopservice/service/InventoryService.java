package com.slow3586.drinkshop.shopservice.service;


import com.slow3586.drinkshop.api.mainservice.entity.Order;
import com.slow3586.drinkshop.api.mainservice.entity.ShopInventory;
import com.slow3586.drinkshop.api.mainservice.topic.OrderTopics;
import com.slow3586.drinkshop.shopservice.repository.ShopInventoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class InventoryService {
    ShopInventoryRepository shopInventoryRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @KafkaListener(topics = OrderTopics.Transaction.PRODUCT, groupId = "inventoryservice", errorHandler = "orderTransactionListenerErrorHandler")
    public void processOrder(Order order) {
        order.getOrderItemList().forEach(orderItem ->
            orderItem.getProduct().getProductInventoryList().forEach(productInventory -> {
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
                productInventory.setShopInventory(shopInventoryRepository.save(shopInventory));
            }));
        kafkaTemplate.send(OrderTopics.Transaction.INVENTORY,
            order.getId(),
            order);
    }

    @KafkaListener(topics = {OrderTopics.Transaction.ERROR}, groupId = "inventoryservice")
    public void orderRevert(Order order) {
        try {
            order.getOrderItemList()
                .forEach(item -> item.getProduct().getProductInventoryList()
                    .forEach(productInventory -> {
                        ShopInventory shopInventory = shopInventoryRepository.findByShopIdAndProductInventoryTypeId(
                            order.getShopId(),
                            productInventory.getProductInventoryTypeId()
                        ).orElseThrow();
                        shopInventoryRepository.save(shopInventory
                            .setReserved(shopInventory.getReserved() - productInventory.getQuantity() * item.getQuantity()));
                    }));
        } catch (Exception e) {
            log.error("#InventoryService#orderRevert: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = {OrderTopics.Transaction.PAID}, groupId = "inventoryservice")
    public void orderPaid(Order order) {
        try {
            order.getOrderItemList()
                .forEach(item -> item.getProduct().getProductInventoryList()
                    .forEach(productInventory -> {
                        ShopInventory shopInventory = shopInventoryRepository.findByShopIdAndProductInventoryTypeId(
                            order.getShopId(),
                            productInventory.getProductInventoryTypeId()
                        ).orElseThrow();
                        int quantity = productInventory.getQuantity() * item.getQuantity();
                        shopInventoryRepository.save(shopInventory
                            .setReserved(shopInventory.getReserved() - quantity)
                            .setQuantity(shopInventory.getQuantity() - quantity));
                    }));
        } catch (Exception e) {
            log.error("InventoryService#orderPaid: {}", e.getMessage(), e);
        }
    }

    public List<ShopInventory> findAllByShopId(UUID id) {
        return shopInventoryRepository.findAllByShopId(id);
    }
}
