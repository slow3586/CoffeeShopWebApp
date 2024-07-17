package com.slow3586.drinkshop.customerservice.service;


import com.slow3586.drinkshop.api.QrCodeUtils;
import com.slow3586.drinkshop.api.mainservice.dto.GetQrCodeResponse;
import com.slow3586.drinkshop.api.mainservice.entity.Customer;
import com.slow3586.drinkshop.customerservice.repository.CustomerRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class CustomerService {
    CustomerRepository customerRepository;
    QrCodeUtils qrCodeUtils;

    public Customer findById(UUID uuid) {
        return customerRepository.findById(uuid)
            .orElseThrow(() -> new IllegalArgumentException(
                "Customer with UUID " + uuid + " does not exist!"));
    }

    public Customer findOrCreateByTelegramId(String telegramId) {
        return customerRepository.findByTelegramId(telegramId)
            .orElseGet(() -> customerRepository.save(
                new Customer().setTelegramId(telegramId)));
    }

    public Customer updateContact(Customer customer) {
        UUID id = customer.getId();
        String phone = customer.getPhoneNumber();
        String name = customer.getName();

        if (!phone.startsWith("+7") || phone.length() != 12) {
            throw new IllegalArgumentException();
        }

        return customerRepository.findById(id)
            .map(c -> c.setPhoneNumber(phone).setName(name))
            .map(customerRepository::save)
            .orElseThrow(() -> new IllegalArgumentException(
                "Customer with ID " + id + " does not exist!"));
    }

    public Customer findByQrCode(String qrCode) {
        return customerRepository.findByQrCodeAndQrCodeExpiresAtAfter(qrCode, Instant.now()).get();
    }

    public GetQrCodeResponse getQrCode(String telegramId) {
        Customer customer = customerRepository.findByTelegramId(telegramId)
            .orElseThrow();

        if (customer.getQrCode() == null || customer.getQrCodeExpiresAt().isBefore(Instant.now())) {
            final String code = String.valueOf(
                    LocalTime.now(ZoneId.of("UTC"))
                        .get(ChronoField.MILLI_OF_DAY) + 10_000_000)
                .substring(0, 6);

            customer.setQrCode(code);
            customer.setQrCodeExpiresAt(Instant.now().plusSeconds(300));
            customer = customerRepository.save(customer);
        }

        final byte[] image = qrCodeUtils.generateQRCodeImage(customer.getQrCode());

        return GetQrCodeResponse.builder()
            .code(customer.getQrCode())
            .duration(Duration.ofMinutes(5))
            .image(image)
            .build();
    }
}
