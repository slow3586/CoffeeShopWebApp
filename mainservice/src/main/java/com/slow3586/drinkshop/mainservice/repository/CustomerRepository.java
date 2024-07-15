package com.slow3586.drinkshop.mainservice.repository;

import com.slow3586.drinkshop.api.VavrRepository;
import com.slow3586.drinkshop.api.mainservice.entity.Customer;
import io.vavr.collection.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Repository
@Transactional(transactionManager = "transactionManager")
public interface CustomerRepository extends VavrRepository<Customer> {
    Customer getByTelegramId(String telegramId);

    @Query("SELECT id, telegram_id FROM customer c WHERE c.phone_number IS NOT NULL AND c.blocked_reason IS NULL")
    List<Customer> findValidForPromos();

    Customer findByQrCodeAndQrCodeExpiresAtAfter(String qrCode, Instant qrCodeExpiresAt);
}
