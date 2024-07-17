package com.slow3586.drinkshop.mainservice.controller;


import com.slow3586.drinkshop.api.mainservice.dto.PaymentSystemUpdate;
import com.slow3586.drinkshop.api.mainservice.topic.PaymentTopics;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class PaymentController {
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @PostMapping("receive")
    @Transactional(transactionManager = "kafkaTransactionManager")
    public void receiveUpdate(PaymentSystemUpdate update) {
        kafkaTemplate.send(PaymentTopics.REQUEST_SYSTEM_RESPONSE,
            update.getOrderId(),
            update);
    }
}
