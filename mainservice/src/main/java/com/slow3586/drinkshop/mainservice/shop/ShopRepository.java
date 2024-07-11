package com.slow3586.drinkshop.mainservice.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public interface ShopRepository extends JpaRepository<ShopEntity, UUID> {
    @Override
    List<ShopEntity> findAll();
}
