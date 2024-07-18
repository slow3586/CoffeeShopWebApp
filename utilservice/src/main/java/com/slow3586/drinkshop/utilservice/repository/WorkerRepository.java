package com.slow3586.drinkshop.utilservice.repository;


import com.slow3586.drinkshop.api.mainservice.entity.Worker;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface WorkerRepository extends ListCrudRepository<Worker, UUID> {
    Optional<Worker> findByTelegramId(String telegramUserId);

    Optional<Worker> findByPhoneNumber(String phoneNumber);
}
