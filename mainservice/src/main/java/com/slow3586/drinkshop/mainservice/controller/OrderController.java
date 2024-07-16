package com.slow3586.drinkshop.mainservice.controller;

import com.slow3586.drinkshop.api.mainservice.OrderRequest;
import com.slow3586.drinkshop.api.mainservice.entity.Order;
import com.slow3586.drinkshop.mainservice.repository.OrderItemRepository;
import com.slow3586.drinkshop.mainservice.repository.OrderRepository;
import com.slow3586.drinkshop.mainservice.service.CustomerService;
import com.slow3586.drinkshop.mainservice.service.ProductService;
import com.slow3586.drinkshop.mainservice.service.ShopService;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
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

@Slf4j
@RestController
@RequestMapping("api/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class OrderController {
    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    TransactionTemplate transactionTemplate;
    KafkaTemplate<UUID, Object> kafkaTemplate;
    CustomerService customerService;
    ShopService shopService;
    ProductService productService;

    @GetMapping("findAllActiveByShopId/{shopId}")
    @Secured({"SYSTEM", "CASHIER", "ADMIN"})
    public List<Order> findAllActiveByShopId(@PathVariable UUID shopId) {
        return orderRepository.findAllActiveByShopId(shopId)
            .map(order -> order
                .setCustomer(order.getCustomerId() != null
                    ? customerService.findById(order.getCustomerId())
                    : null)
                .setShop(shopService.findById(order.getShopId()))
                .setOrderItemList(
                    orderItemRepository.findAllByOrderId(order.getId())
                        .map(i -> i.setProduct(productService.findById(i.getProductId())))));
    }

    @PostMapping("create")
    @Secured({"SYSTEM", "CASHIER", "ADMIN"})
    public void create(@RequestBody @NonNull OrderRequest orderRequest) {
        kafkaTemplate.send("order.request", orderRequest);
    }

    @PostMapping("complete")
    @Secured({"SYSTEM", "CASHIER", "ADMIN"})
    public void completeOrder(UUID uuid) {
        kafkaTemplate.send("order.complete", uuid);
    }
}
