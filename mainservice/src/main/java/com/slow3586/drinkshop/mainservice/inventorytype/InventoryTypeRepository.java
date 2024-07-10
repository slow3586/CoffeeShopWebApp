package com.slow3586.drinkshop.mainservice.inventorytype;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryTypeRepository extends JpaRepository<InventoryTypeEntity, UUID> {
}
