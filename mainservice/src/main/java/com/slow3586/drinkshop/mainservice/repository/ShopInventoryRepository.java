package com.slow3586.drinkshop.mainservice.repository;

import com.slow3586.drinkshop.api.mainservice.entity.ShopInventory;
import io.vavr.control.Option;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface ShopInventoryRepository extends ListCrudRepository<ShopInventory, UUID> {
    Option<ShopInventory> findByShopIdAndInventoryTypeId(UUID shopId, UUID inventoryTypeId);
}
