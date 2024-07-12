package com.slow3586.drinkshop.mainservice.repository;

import com.slow3586.drinkshop.api.mainservice.entity.CustomerOrder;
import io.vavr.collection.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface CustomerOrderRepository extends ListCrudRepository<CustomerOrder, UUID> {
    @Query("select * from customer_order p order by p.created_at offset :offset limit 10")
    List<CustomerOrder> query(int offset);
}
