package com.slow3586.drinkshop.mainservice.controller;

import com.slow3586.drinkshop.api.mainservice.dto.OrderRequest;
import com.slow3586.drinkshop.api.mainservice.entity.Order;
import com.slow3586.drinkshop.api.mainservice.topic.OrderTopics;
import com.slow3586.drinkshop.mainservice.service.CustomerService;
import com.slow3586.drinkshop.mainservice.service.OrderService;
import com.slow3586.drinkshop.mainservice.service.ProductService;
import com.slow3586.drinkshop.mainservice.service.ShopService;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("api/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class OrderController {
    OrderService orderService;
    ReplyingKafkaTemplate<UUID, Object, Object> replyingKafkaTemplate;
    CustomerService customerService;
    ShopService shopService;
    ProductService productService;

    @GetMapping("findAllActiveByShopId/{shopId}")
    @Secured({"SYSTEM", "CASHIER", "ADMIN"})
    public List<Order> findAllActiveByShopId(@PathVariable UUID shopId) {
        return orderService.findAllActiveByShopId(shopId);
    }

    @PostMapping("create")
    @Secured({"SYSTEM", "CASHIER", "ADMIN"})
    @Transactional(transactionManager = "kafkaTransactionManager")
    public CompletableFuture<UUID> create(@RequestBody @NonNull OrderRequest orderRequest) {
        return replyingKafkaTemplate.sendAndReceive(
                new ProducerRecord<>(OrderTopics.Request.REQUEST_CREATE, orderRequest))
            .thenApply(ConsumerRecord::value)
            .thenApply(o -> ((UUID) o))
            .toCompletableFuture();
    }

    @PostMapping("complete")
    @Secured({"SYSTEM", "CASHIER", "ADMIN"})
    @Transactional(transactionManager = "kafkaTransactionManager")
    public CompletableFuture<UUID> complete(UUID uuid) {
        return replyingKafkaTemplate.sendAndReceive(
                new ProducerRecord<>(OrderTopics.Request.REQUEST_COMPLETED, uuid))
            .thenApply(ConsumerRecord::value)
            .thenApply(o -> ((UUID) o))
            .toCompletableFuture();
    }
}
