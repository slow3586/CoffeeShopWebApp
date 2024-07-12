package com.slow3586.drinkshop.mainservice.repository;

import com.slow3586.drinkshop.api.mainservice.entity.CustomerOrder;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface CustomerOrderRepository extends ListCrudRepository<CustomerOrder, UUID> {
}