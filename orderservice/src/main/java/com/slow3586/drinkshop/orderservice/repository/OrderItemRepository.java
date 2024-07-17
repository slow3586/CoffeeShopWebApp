package com.slow3586.drinkshop.orderservice.repository;


import com.slow3586.drinkshop.api.mainservice.entity.OrderItem;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface OrderItemRepository extends ListCrudRepository<OrderItem, UUID> {
    List<OrderItem> findAllByOrderId(UUID productId);
}
