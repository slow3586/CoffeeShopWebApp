package com.slow3586.drinkshop.mainservice.repository;

import com.slow3586.drinkshop.api.mainservice.entity.Customer;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface CustomerRepository extends ListCrudRepository<Customer, UUID> {
    Customer getByTelegramId(String telegramId);

    @Query("SELECT id, telegram_id FROM customer c WHERE c.phone_number IS NOT NULL AND c.blocked_reason IS NULL")
    List<Customer> findValidForPromos();

    Customer findByQrCodeAndQrCodeExpiresAtAfter(String qrCode, Instant qrCodeExpiresAt);
}
