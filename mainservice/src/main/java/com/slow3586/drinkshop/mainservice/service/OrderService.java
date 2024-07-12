package com.slow3586.drinkshop.mainservice.service;

import com.slow3586.drinkshop.api.mainservice.OrderRequest;
import com.slow3586.drinkshop.api.mainservice.OrderTransaction;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.streams.StreamsBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class OrderService {
    StreamsBuilder streamsBuilder;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @Transactional(transactionManager = "kafkaTransactionManager")
    @PostMapping("create_order")
    public void createOrder(@RequestBody @NonNull OrderRequest orderRequest) {
        kafkaTemplate.send(
            "order_transaction",
            UUID.randomUUID(),
            new OrderTransaction().setOrderRequest(orderRequest));
    }

    @Bean
    public NewTopic orderTransactionTopic() {
        return TopicBuilder.name("order_transaction")
            .replicas(1)
            .partitions(1)
            .compact()
            .build();
    }
}
