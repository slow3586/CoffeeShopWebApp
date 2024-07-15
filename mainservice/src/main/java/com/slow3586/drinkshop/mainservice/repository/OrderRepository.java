package com.slow3586.drinkshop.mainservice.repository;

import com.slow3586.drinkshop.api.VavrRepository;
import com.slow3586.drinkshop.api.mainservice.entity.Order;
import io.vavr.collection.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface OrderRepository extends VavrRepository<Order> {
    @Query("select * from customer_order o " +
        "where o.shop_id = :shopId " +
        "order by o.created_at limit 100")
    List<Order> findAllActive(UUID shopId);
}
