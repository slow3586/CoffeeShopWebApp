package com.slow3586.drinkshop.shopservice.repository;


import com.slow3586.drinkshop.api.mainservice.entity.ShopShift;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface ShopShiftRepository extends ListCrudRepository<ShopShift, UUID> {
    List<ShopShift> findByShopId(UUID shopId);
}
