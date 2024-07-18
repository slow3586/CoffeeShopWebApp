package com.slow3586.drinkshop.customerservice.listener;

import com.slow3586.drinkshop.api.mainservice.entity.Order;
import com.slow3586.drinkshop.api.mainservice.entity.TelegramPublish;
import com.slow3586.drinkshop.api.mainservice.topic.CustomerTelegramTopics;
import com.slow3586.drinkshop.api.mainservice.topic.OrderTopics;
import com.slow3586.drinkshop.customerservice.repository.CustomerRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class CustomerListener {
    CustomerRepository customerRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @KafkaListener(topics = OrderTopics.Transaction.CREATED, errorHandler = "orderTransactionListenerErrorHandler")
    public void processOrder(Order order) {
        kafkaTemplate.send(OrderTopics.Transaction.CUSTOMER,
            order.getId(),
            order.setCustomer(Optional.ofNullable(order.getCustomerId())
                .map(c -> customerRepository.findById(c).orElseThrow())
                .orElse(null)));
    }

    @KafkaListener(topics = CustomerTelegramTopics.Transaction.CREATED)
    public void processTelegramPublish(TelegramPublish telegramPublish) {
        kafkaTemplate.send(CustomerTelegramTopics.Transaction.WITH_CUSTOMERS,
            telegramPublish.getId(),
            telegramPublish.setCustomerList(
                customerRepository.findAll()));
    }
}
