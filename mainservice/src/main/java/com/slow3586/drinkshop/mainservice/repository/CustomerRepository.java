package com.slow3586.drinkshop.mainservice.repository;

import com.slow3586.drinkshop.mainservice.entity.Customer;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public interface CustomerRepository extends ListCrudRepository<Customer, UUID> {
    Customer getByTelegramId(String telegramId);

    @Query("SELECT * FROM customer c WHERE c.phone_number IS NOT NULL AND c.blocked_reason IS NULL")
    List<Customer> findValidForPromos();
}
