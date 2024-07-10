package com.slow3586.drinkshop.mainservice.product;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends CrudRepository<ProductEntity, UUID> {
    List<ProductEntity> findAll();
}
