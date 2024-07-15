package com.slow3586.drinkshop.mainservice.service;


import com.slow3586.drinkshop.api.mainservice.entity.CustomerOrder;
import com.slow3586.drinkshop.api.mainservice.entity.Payment;
import com.slow3586.drinkshop.api.mainservice.entity.PaymentCheck;
import com.slow3586.drinkshop.mainservice.repository.CustomerOrderRepository;
import com.slow3586.drinkshop.mainservice.repository.PaymentCheckRepository;
import com.slow3586.drinkshop.mainservice.repository.PaymentRepository;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("api/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@Slf4j
public class PaymentService {
    PaymentRepository paymentRepository;
    PaymentCheckRepository paymentCheckRepository;
    CustomerOrderRepository customerOrderRepository;
    TransactionTemplate transactionTemplate;

    @Data
    @Accessors(chain = true)
    public static class PaymentSystemUpdate {
        UUID paymentId;
        String status;
    }

    @PostMapping("receive")
    public void updateCheckFromPaymentSystem(PaymentSystemUpdate update) {
        paymentCheckRepository.save(
            new PaymentCheck()
                .setPaymentId(update.getPaymentId())
                .setPaymentSystemStatus(update.getStatus())
                .setStatus("RECEIVED"));
    }

    @Async
    @Scheduled(fixedRate = 100)
    public void processChecks() {
        paymentCheckRepository.findByStatus("SUCCESS").forEach(c ->
            transactionTemplate.executeWithoutResult((transactionStatus) -> {
                try {
                    Payment payment = paymentRepository.findById(c.getPaymentId())
                        .getOrElseThrow(() -> new RuntimeException("Payment not found"));
                    CustomerOrder customerOrder = customerOrderRepository.findById(payment.getOrderId())
                        .getOrElseThrow(() -> new RuntimeException("Customer order not found"));
                    customerOrder.setPaidAt(Instant.now());
                    customerOrderRepository.save(customerOrder);
                    c.setStatus("PROCESSED");
                } catch (Exception e) {
                    log.error("#processChecks", e);
                    c.setStatus("ERROR");
                }
                paymentCheckRepository.save(c);
            }));
    }
}
