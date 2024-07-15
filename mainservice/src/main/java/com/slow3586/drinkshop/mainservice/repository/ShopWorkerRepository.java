package com.slow3586.drinkshop.mainservice.repository;

import com.slow3586.drinkshop.api.VavrRepository;
import com.slow3586.drinkshop.api.mainservice.entity.Worker;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface ShopWorkerRepository extends VavrRepository<Worker> {
}
