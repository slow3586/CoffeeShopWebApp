package com.slow3586.drinkshop.mainservice.producttype;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductTypeRepository extends JpaRepository<ProductTypeEntity, UUID> {
}
