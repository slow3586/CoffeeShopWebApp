package com.slow3586.drinkshop.mainservice.service;


import com.slow3586.drinkshop.api.mainservice.GetQrCodeResponse;
import com.slow3586.drinkshop.api.mainservice.entity.Customer;
import com.slow3586.drinkshop.api.mainservice.entity.Order;
import com.slow3586.drinkshop.api.mainservice.entity.TelegramPublish;
import com.slow3586.drinkshop.api.mainservice.topic.OrderTopics;
import com.slow3586.drinkshop.api.mainservice.topic.CustomerTelegramTopics;
import com.slow3586.drinkshop.mainservice.repository.CustomerRepository;
import com.slow3586.drinkshop.mainservice.utils.QrCodeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class CustomerService {
    CustomerRepository customerRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;
    QrCodeUtils qrCodeUtils;

    public Customer findById(UUID uuid) {
        return customerRepository.findById(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Customer with UUID " + uuid + " does not exist!"));
    }

    public Customer findOrCreateByTelegramId(String telegramId) {
        return customerRepository.findByTelegramId(telegramId)
            .orElseGet(() -> customerRepository.save(
                new Customer().setTelegramId(telegramId)));
    }

    public Customer updateContact(UUID customerId, String phone, String name) {
        if (!phone.startsWith("+7") || phone.length() != 12) {
            throw new IllegalArgumentException();
        }

        return customerRepository.findById(customerId)
            .map(c -> c.setPhoneNumber(phone).setName(name))
            .map(customerRepository::save)
            .orElseThrow(() -> new IllegalArgumentException("Customer with ID " + customerId + " does not exist!"));
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

    @KafkaListener(topics = OrderTopics.Transaction.CREATED, groupId = "customerservice")
    public void processOrder(Order order) {
        try {
            kafkaTemplate.send(OrderTopics.Transaction.CUSTOMER,
                order.getId(),
                order.setCustomer(Optional.ofNullable(order.getCustomerId())
                    .flatMap(customerRepository::findById)
                    .orElseThrow()));
        } catch (Exception e) {
            log.error("CustomerService#processOrder: {}", e.getMessage(), e);
            kafkaTemplate.send(OrderTopics.Transaction.ERROR,
                order.getId(),
                order.setError(e.getMessage()));
        }
    }

    @KafkaListener(topics = CustomerTelegramTopics.Transaction.CREATED, groupId = "customerservice")
    public void processTelegramPublish(TelegramPublish telegramPublish) {
        kafkaTemplate.send(CustomerTelegramTopics.Transaction.WITH_CUSTOMERS,
            telegramPublish.getId(),
            telegramPublish.setCustomerList(
                customerRepository.findAll()));
    }
}
