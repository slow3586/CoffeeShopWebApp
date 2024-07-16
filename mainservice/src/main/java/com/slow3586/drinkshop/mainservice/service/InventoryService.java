package com.slow3586.drinkshop.mainservice.service;


import com.slow3586.drinkshop.api.mainservice.entity.Order;
import com.slow3586.drinkshop.api.mainservice.entity.ShopInventory;
import com.slow3586.drinkshop.mainservice.repository.ShopInventoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/customer")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class InventoryService {
    ShopInventoryRepository shopInventoryRepository;

    @KafkaListener(topics = "order.product")
    public void processOrder(Order order) {
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
            }));
    }
}
