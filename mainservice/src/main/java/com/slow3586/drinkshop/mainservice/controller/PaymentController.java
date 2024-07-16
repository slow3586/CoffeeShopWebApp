package com.slow3586.drinkshop.mainservice.controller;


import com.slow3586.drinkshop.api.mainservice.PaymentSystemUpdate;
import com.slow3586.drinkshop.api.mainservice.PaymentTopics;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
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
    public void receiveUpdate(PaymentSystemUpdate update) {
        kafkaTemplate.send(PaymentTopics.REQUEST_SYSTEM_RESPONSE,
            update.getPaymentId(),
            update);
    }
}
