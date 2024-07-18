package com.slow3586.drinkshop.orderservice.repository;


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
    @Query("select * from \"order\" o " +
        "where o.shop_id = :shopId AND" +
        "(o.status = 'CREATED' OR o.status = 'PAYMENT' OR o.status = 'PAID')" +
        "order by o.created_at limit 100")
    List<Order> findAllActiveByShopId(UUID shopId);
    @Query("select * from \"order\" o " +
        "where " +
        "(o.status = 'CREATED' AND o.created_at + INTERVAL '1 minute' < NOW())" +
        "OR (o.status = 'PAYMENT' AND o.created_at + INTERVAL '2 minutes' < NOW())" +
        "OR (o.status = 'PAID' AND o.created_at + INTERVAL '70 minutes' < NOW())" +
        "order by o.created_at limit 100")
    List<Order> findBadOrders();
}
