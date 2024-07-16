package com.slow3586.drinkshop.mainservice.controller;

import com.slow3586.drinkshop.api.mainservice.OrderRequest;
import com.slow3586.drinkshop.api.mainservice.OrderTopics;
import com.slow3586.drinkshop.api.mainservice.entity.Order;
import com.slow3586.drinkshop.mainservice.repository.OrderItemRepository;
import com.slow3586.drinkshop.mainservice.repository.OrderRepository;
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
import org.springframework.transaction.support.TransactionTemplate;
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
    TransactionTemplate transactionTemplate;
    ReplyingKafkaTemplate<UUID, Object, UUID> replyingKafkaTemplate;
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
    public CompletableFuture<UUID> create(@RequestBody @NonNull OrderRequest orderRequest) {
        return replyingKafkaTemplate.sendAndReceive(
                new ProducerRecord<>(OrderTopics.REQUEST_CREATE, orderRequest))
            .thenApply(ConsumerRecord::value)
            .toCompletableFuture();
    }

    @PostMapping("complete")
    @Secured({"SYSTEM", "CASHIER", "ADMIN"})
    public CompletableFuture<UUID> complete(UUID uuid) {
        return replyingKafkaTemplate.sendAndReceive(
                new ProducerRecord<>(OrderTopics.REQUEST_COMPLETE, uuid))
            .thenApply(ConsumerRecord::value)
            .toCompletableFuture();
    }
}
