package com.slow3586.drinkshop.mainservice.shopshift;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShopShiftRepository extends JpaRepository<ShopShiftEntity, UUID> {
    @Override
    List<ShopShiftEntity> findAll();
}
