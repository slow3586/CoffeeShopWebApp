package com.slow3586.drinkshop.mainservice.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
}
