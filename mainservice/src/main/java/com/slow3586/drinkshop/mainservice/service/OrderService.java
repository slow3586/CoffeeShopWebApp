package com.slow3586.drinkshop.mainservice.service;

import com.slow3586.drinkshop.api.mainservice.OrderRequest;
import com.slow3586.drinkshop.api.mainservice.entity.Customer;
import com.slow3586.drinkshop.api.mainservice.entity.CustomerOrder;
import com.slow3586.drinkshop.api.mainservice.entity.CustomerOrderItem;
import com.slow3586.drinkshop.api.mainservice.entity.ShopInventory;
import com.slow3586.drinkshop.api.mainservice.entity.TelegramPublish;
import com.slow3586.drinkshop.mainservice.repository.CustomerOrderItemRepository;
import com.slow3586.drinkshop.mainservice.repository.CustomerOrderRepository;
import com.slow3586.drinkshop.mainservice.repository.CustomerRepository;
import com.slow3586.drinkshop.mainservice.repository.ProductInventoryRepository;
import com.slow3586.drinkshop.mainservice.repository.ProductRepository;
import com.slow3586.drinkshop.mainservice.repository.ShopInventoryRepository;
import com.slow3586.drinkshop.mainservice.repository.TelegramPublishRepository;
import io.vavr.Tuple;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import jakarta.ws.rs.QueryParam;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("api/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class OrderService {
    CustomerOrderRepository customerOrderRepository;
    ProductRepository productRepository;
    ShopInventoryRepository shopInventoryRepository;
    CustomerOrderItemRepository customerOrderItemRepository;
    ProductInventoryRepository productInventoryRepository;
    TelegramPublishRepository telegramPublishRepository;
    CustomerRepository customerRepository;

    @GetMapping("query")
    public List<CustomerOrder> query(@QueryParam("page") Integer page) {
        return customerOrderRepository.query(
            Optional.ofNullable(page).orElse(0) * 10);
    }

    @Transactional
    @PostMapping("create")
    public void create(@RequestBody @NonNull OrderRequest orderRequest) {
        final CustomerOrder customerOrder =
            customerOrderRepository.save(
                new CustomerOrder()
                    .customerId(orderRequest.getCustomerId())
                    .shopId(orderRequest.getShopId()));

        List<CustomerOrderItem> customerOrderItems = List.ofAll(
            customerOrderItemRepository.saveAll(
                orderRequest.getProductQuantityList()
                    .map(e -> new CustomerOrderItem()
                        .orderId(customerOrder.id())
                        .productTypeId(e.getProductId())
                        .quantity(e.getQuantity()))
                    .toList()));

        customerOrderItems.forEach((customerOrderItem) -> {
            final Map<ShopInventory, Integer> newQuantityMap =
                productInventoryRepository.findByProductId(customerOrderItem.productTypeId())
                    .toMap(productInventory -> Tuple.of(
                        productInventory,
                        shopInventoryRepository.findByShopIdAndInventoryTypeId(
                            customerOrder.shopId(),
                            productInventory.inventoryId())))
                    .map((productInventory, shopInventory) ->
                        Tuple.of(shopInventory, productInventory.quantity() * customerOrderItem.quantity()));

            newQuantityMap.find(v -> v._1.quantity() - v._1.reserved() - v._2 < 0)
                .forEach(b -> {throw new IllegalStateException("Not enough: " + b._1.id());});

            newQuantityMap.forEach((shopInventory, toReserve) ->
                shopInventoryRepository.save(shopInventory.reserved(
                    shopInventory.reserved() + toReserve)));
        });

        Customer customer = customerRepository.findById(customerOrder.customerId()).orElseThrow();

        telegramPublishRepository.save(new TelegramPublish()
            .telegramId(customer.telegramId())
            .text("Новый заказ: " + customerOrder.id()));
    }
}
