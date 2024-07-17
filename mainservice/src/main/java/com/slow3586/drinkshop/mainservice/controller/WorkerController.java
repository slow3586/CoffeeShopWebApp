package com.slow3586.drinkshop.mainservice.controller;

import com.slow3586.drinkshop.api.mainservice.dto.LoginRequest;
import com.slow3586.drinkshop.api.mainservice.entity.Worker;
import com.slow3586.drinkshop.api.mainservice.topic.WorkerTopics;
import com.slow3586.drinkshop.mainservice.service.WorkerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@RestController("api/worker")
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class WorkerController {
    WorkerService workerService;
    PasswordEncoder passwordEncoder;
    SecretKey secretKey;
    ReplyingKafkaTemplate<UUID, Object, Object> replyingKafkaTemplate;

    @PostMapping("login")
    @Transactional(transactionManager = "kafkaTransactionManager")
    public CompletableFuture<String> login(LoginRequest loginRequest) {
        return replyingKafkaTemplate.sendAndReceive(
            new ProducerRecord<>(
                WorkerTopics.REQUEST_LOGIN, loginRequest))
            .thenApply(ConsumerRecord::value)
            .thenApply(o -> ((String) o))
            .toCompletableFuture();
    }

    @PostMapping("token")
    @Transactional(transactionManager = "kafkaTransactionManager")
    public CompletableFuture<Worker> token(String token) {
        return replyingKafkaTemplate.sendAndReceive(
                new ProducerRecord<>(
                    WorkerTopics.REQUEST_TOKEN, token))
            .thenApply(ConsumerRecord::value)
            .thenApply(o -> ((Worker) o))
            .toCompletableFuture();
    }
}
