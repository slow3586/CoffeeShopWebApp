package com.slow3586.drinkshop.mainservice.repository;

import com.slow3586.drinkshop.api.VavrRepository;
import com.slow3586.drinkshop.api.mainservice.entity.CustomerOrderItem;
import com.slow3586.drinkshop.api.mainservice.entity.ProductInventory;
import io.vavr.collection.List;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface CustomerOrderItemRepository extends VavrRepository<CustomerOrderItem> {
    List<CustomerOrderItem> findAllByOrderId(UUID productId);
}
