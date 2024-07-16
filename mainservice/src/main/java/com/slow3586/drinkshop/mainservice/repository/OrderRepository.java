package com.slow3586.drinkshop.mainservice.repository;


import com.slow3586.drinkshop.api.mainservice.entity.Order;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface OrderRepository extends ListCrudRepository<Order, UUID> {
    @Query("select * from customer_order o " +
        "where o.shop_id = :shopId " +
        "order by o.created_at limit 100")
    List<Order> findAllActiveByShopId(UUID shopId);
}
