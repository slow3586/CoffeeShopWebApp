package com.slow3586.drinkshop.mainservice.repository;

import com.slow3586.drinkshop.api.VavrRepository;
import com.slow3586.drinkshop.api.mainservice.entity.ShopInventory;
import io.vavr.collection.List;
import io.vavr.control.Option;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface ShopInventoryRepository extends VavrRepository<ShopInventory> {
    @Query("select * from product_type p order by p.created_at offset :offset limit 10")
    List<ShopInventory> query(int offset);
    List<ShopInventory> findAllByShopId(UUID shopId);
    Option<ShopInventory> findByShopIdAndProductInventoryTypeId(UUID shopId, UUID productInventoryTypeId);
}
