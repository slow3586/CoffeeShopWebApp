package com.slow3586.greenhousecoffee.mainservice;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerRepository extends CrudRepository<CustomerEntity, UUID> {
    CustomerEntity getByTelegramId(String telegramId);
    CustomerEntity findByQrCode(String qrCode);
}
