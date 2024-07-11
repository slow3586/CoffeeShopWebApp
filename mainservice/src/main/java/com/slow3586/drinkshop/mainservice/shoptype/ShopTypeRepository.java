package com.slow3586.drinkshop.mainservice.shoptype;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public interface ShopTypeRepository extends JpaRepository<ShopTypeEntity, UUID> {
    @Override
    List<ShopTypeEntity> findAll();
}