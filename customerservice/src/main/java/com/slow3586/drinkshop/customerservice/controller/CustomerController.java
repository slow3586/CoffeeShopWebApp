package com.slow3586.drinkshop.customerservice.controller;


import com.slow3586.drinkshop.api.mainservice.client.CustomerClient;
import com.slow3586.drinkshop.api.mainservice.dto.GetQrCodeResponse;
import com.slow3586.drinkshop.api.mainservice.entity.Customer;
import com.slow3586.drinkshop.customerservice.service.CustomerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/customer")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class CustomerController implements CustomerClient {
    CustomerService customerService;

    @GetMapping("findById/{id}")
    public Customer findById(@PathVariable UUID id) {
        return customerService.findById(id);
    }

    @GetMapping("findOrCreateByTelegramId/{id}")
    public Customer findOrCreateByTelegramId(@PathVariable String id) {
        return customerService.findOrCreateByTelegramId(id);
    }

    @GetMapping("findByQrCode/{qrCode}")
    @Secured({"SYSTEM", "CASHIER", "ADMIN"})
    public Customer findByQrCode(@PathVariable String qrCode) {
        return customerService.findByQrCode(qrCode);
    }

    @GetMapping("getQrCode/{id}")
    @Secured({"SYSTEM", "CASHIER", "ADMIN"})
    public GetQrCodeResponse getQrCode(String telegramId) {
        return customerService.getQrCode(telegramId);
    }

    @PostMapping("updateContact")
    @Secured({"SYSTEM", "CASHIER", "ADMIN"})
    public Customer updateContact(@RequestBody Customer customer) {
        return customerService.updateContact(customer);
    }
}
