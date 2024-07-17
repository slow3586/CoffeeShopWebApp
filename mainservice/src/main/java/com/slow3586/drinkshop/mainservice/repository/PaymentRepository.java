package com.slow3586.drinkshop.mainservice.repository;


import com.slow3586.drinkshop.api.mainservice.entity.Payment;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface PaymentRepository extends ListCrudRepository<Payment, UUID> {
    Optional<Payment> findByOrderId(UUID orderId);
    boolean existsByOrderId(UUID orderId);
}