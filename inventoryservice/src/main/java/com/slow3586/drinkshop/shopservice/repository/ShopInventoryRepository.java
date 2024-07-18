package com.slow3586.drinkshop.shopservice.repository;


import com.slow3586.drinkshop.api.mainservice.entity.ShopInventory;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface ShopInventoryRepository extends ListCrudRepository<ShopInventory, UUID> {
    @Query("select * from product_type p order by p.created_at offset :offset limit 10")
    List<ShopInventory> query(int offset);
    List<ShopInventory> findAllByShopId(UUID shopId);
    Optional<ShopInventory> findByShopIdAndProductInventoryTypeId(UUID shopId, UUID productInventoryTypeId);
}
