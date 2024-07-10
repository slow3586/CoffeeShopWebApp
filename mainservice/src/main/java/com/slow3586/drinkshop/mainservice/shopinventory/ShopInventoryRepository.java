package com.slow3586.drinkshop.mainservice.shopinventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShopInventoryRepository extends JpaRepository<ShopInventoryEntity, UUID> {
}
