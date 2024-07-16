package com.slow3586.drinkshop.mainservice.repository;


import com.slow3586.drinkshop.api.mainservice.entity.ProductInventory;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface ProductInventoryRepository extends ListCrudRepository<ProductInventory, UUID> {
    @Query("select * from product_inventory p order by p.created_at offset :offset limit 10")
    List<ProductInventory> query(int offset);

    List<ProductInventory> findByProductId(UUID productId);
}
