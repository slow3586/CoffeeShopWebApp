package com.slow3586.drinkshop.mainservice.service;


import com.slow3586.drinkshop.api.mainservice.entity.Customer;
import com.slow3586.drinkshop.api.mainservice.entity.Order;
import com.slow3586.drinkshop.api.mainservice.entity.TelegramPublish;
import com.slow3586.drinkshop.api.mainservice.topic.OrderTopics;
import com.slow3586.drinkshop.api.mainservice.topic.TelegramTopics;
import com.slow3586.drinkshop.mainservice.repository.CustomerRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class CustomerService {
    CustomerRepository customerRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    public Customer findById(UUID uuid) {
        return customerRepository.findById(uuid).get();
    }

    public Customer findByTelegramId(String telegramUserId) {
        return customerRepository.findByTelegramId(telegramUserId).get();
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @KafkaListener(topics = OrderTopics.TRANSACTION_CREATED)
    public void processOrder(Order order) {
        try {
            kafkaTemplate.send(OrderTopics.TRANSACTION_CUSTOMER,
                order.getId(),
                order.setCustomer(Optional.ofNullable(order.getCustomerId())
                    .flatMap(customerRepository::findById)
                    .get()));
        } catch (Exception e) {
            log.error("CustomerService#processOrder: {}", e.getMessage(), e);
            kafkaTemplate.send(OrderTopics.TRANSACTION_CUSTOMER_ERROR,
                order.getId(),
                e.getMessage());
        }
    }

    public Customer findByQrCode(String qrCode) {
        return customerRepository.findByQrCodeAndQrCodeExpiresAtAfter(qrCode, Instant.now()).get();
    }
}
