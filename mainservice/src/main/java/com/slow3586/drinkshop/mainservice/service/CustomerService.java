package com.slow3586.drinkshop.mainservice.service;


import com.slow3586.drinkshop.api.mainservice.entity.Order;
import com.slow3586.drinkshop.mainservice.repository.CustomerRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/customer")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class CustomerService {
    CustomerRepository customerRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @KafkaListener(topics = "order.created")
    public void processOrder(Order order) {
        kafkaTemplate.send("order.customer",
            order.getId(),
            order.setCustomer(Optional.ofNullable(order.getCustomerId())
                .flatMap(c -> customerRepository.findById(order.getCustomerId()))
                .get()));
    }
}
