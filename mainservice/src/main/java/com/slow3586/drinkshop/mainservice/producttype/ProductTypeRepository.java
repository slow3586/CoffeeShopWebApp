package com.slow3586.drinkshop.mainservice.producttype;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional
public interface ProductTypeRepository extends JpaRepository<ProductTypeEntity, UUID> {
}
