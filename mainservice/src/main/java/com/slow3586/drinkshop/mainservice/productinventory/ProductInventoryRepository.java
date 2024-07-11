package com.slow3586.drinkshop.mainservice.productinventory;

import com.slow3586.drinkshop.mainservice.promo.PromoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public interface ProductInventoryRepository extends JpaRepository<ProductInventoryEntity, UUID> {
    List<ProductInventoryEntity> findAll();
}
