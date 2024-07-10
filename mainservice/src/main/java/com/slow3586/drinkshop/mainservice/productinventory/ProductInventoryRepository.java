package com.slow3586.drinkshop.mainservice.productinventory;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventoryEntity, UUID> {
    List<ProductInventoryEntity> findAll();
}
