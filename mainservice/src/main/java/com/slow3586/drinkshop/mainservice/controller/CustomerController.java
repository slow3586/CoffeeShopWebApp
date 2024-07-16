package com.slow3586.drinkshop.mainservice.controller;


import com.slow3586.drinkshop.api.mainservice.entity.Customer;
import com.slow3586.drinkshop.mainservice.repository.CustomerRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("api/customer")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class CustomerController {
    CustomerRepository customerRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @GetMapping("findById/{id}")
    public Customer findById(@PathVariable UUID id) {
        return customerRepository.findById(id).get();
    }

    @GetMapping("findByQrCode/{qrCode}")
    @Secured({"SYSTEM", "CASHIER", "ADMIN"})
    public Customer findByQrCode(@PathVariable String qrCode) {
        return customerRepository.findByQrCodeAndQrCodeExpiresAtAfter(qrCode, Instant.now());
    }
}