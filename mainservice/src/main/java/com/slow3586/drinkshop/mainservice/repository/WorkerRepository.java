package com.slow3586.drinkshop.mainservice.repository;


import com.slow3586.drinkshop.api.mainservice.entity.Worker;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface WorkerRepository extends ListCrudRepository<Worker, UUID> {
    Worker findByTelegramId(String telegramUserId);

    Worker findByPhoneNumber(String phoneNumber);
}
