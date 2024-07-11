package com.slow3586.drinkshop.mainservice.repository;

import com.slow3586.drinkshop.mainservice.entity.ShopShift;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional
public interface ShopShiftRepository extends ListCrudRepository<ShopShift, UUID> {
}
