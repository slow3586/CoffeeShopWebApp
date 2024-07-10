package com.slow3586.drinkshop.mainservice.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
    CustomerEntity getByTelegramId(String telegramId);
    CustomerEntity findByQrCode(String qrCode);
}
