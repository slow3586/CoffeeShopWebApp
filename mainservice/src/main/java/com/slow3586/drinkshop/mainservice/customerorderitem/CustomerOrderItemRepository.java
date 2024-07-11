package com.slow3586.drinkshop.mainservice.customerorderitem;

import com.slow3586.drinkshop.mainservice.promo.PromoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional
public interface CustomerOrderItemRepository extends JpaRepository<CustomerOrderItemEntity, UUID> {
}
