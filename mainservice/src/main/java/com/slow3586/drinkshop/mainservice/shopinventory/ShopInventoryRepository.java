package com.slow3586.drinkshop.mainservice.shopinventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional
public interface ShopInventoryRepository extends JpaRepository<ShopInventoryEntity, UUID> {
}
