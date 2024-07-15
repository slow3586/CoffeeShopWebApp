package com.slow3586.drinkshop.mainservice.service;


import com.slow3586.drinkshop.api.mainservice.entity.Customer;
import com.slow3586.drinkshop.mainservice.repository.CustomerRepository;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("api/customer")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class CustomerService {
    CustomerRepository customerRepository;

    @GetMapping("findByQrCode/{qrCode}")
    @Secured({"SYSTEM", "CASHIER", "ADMIN"})
    public Customer findByQrCode(@PathVariable String qrCode) {
        return customerRepository.findByQrCodeAndQrCodeExpiresAtAfter(qrCode, Instant.now());
    }
}
