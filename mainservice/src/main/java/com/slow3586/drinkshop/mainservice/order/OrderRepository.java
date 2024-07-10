package com.slow3586.drinkshop.mainservice.order;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends CrudRepository<OrderEntity, UUID> {
    @Override
    List<OrderEntity> findAll();
}
