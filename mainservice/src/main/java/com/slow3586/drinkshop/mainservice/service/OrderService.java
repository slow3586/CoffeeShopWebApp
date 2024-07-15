package com.slow3586.drinkshop.mainservice.service;

import com.slow3586.drinkshop.api.mainservice.OrderRequest;
import com.slow3586.drinkshop.api.mainservice.entity.Customer;
import com.slow3586.drinkshop.api.mainservice.entity.Order;
import com.slow3586.drinkshop.api.mainservice.entity.OrderItem;
import com.slow3586.drinkshop.api.mainservice.entity.Payment;
import com.slow3586.drinkshop.api.mainservice.entity.Product;
import com.slow3586.drinkshop.api.mainservice.entity.ProductInventory;
import com.slow3586.drinkshop.api.mainservice.entity.Shop;
import com.slow3586.drinkshop.api.mainservice.entity.ShopInventory;
import com.slow3586.drinkshop.api.mainservice.entity.TelegramPublish;
import com.slow3586.drinkshop.mainservice.repository.OrderItemRepository;
import com.slow3586.drinkshop.mainservice.repository.OrderRepository;
import com.slow3586.drinkshop.mainservice.repository.CustomerRepository;
import com.slow3586.drinkshop.mainservice.repository.PaymentCheckRepository;
import com.slow3586.drinkshop.mainservice.repository.PaymentRepository;
import com.slow3586.drinkshop.mainservice.repository.ProductInventoryRepository;
import com.slow3586.drinkshop.mainservice.repository.ProductInventoryTypeRepository;
import com.slow3586.drinkshop.mainservice.repository.ProductRepository;
import com.slow3586.drinkshop.mainservice.repository.ProductGroupRepository;
import com.slow3586.drinkshop.mainservice.repository.ShopInventoryRepository;
import com.slow3586.drinkshop.mainservice.repository.ShopRepository;
import com.slow3586.drinkshop.mainservice.repository.TelegramPublishRepository;
import io.vavr.Tuple;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class OrderService {
    OrderRepository orderRepository;
    ShopInventoryRepository shopInventoryRepository;
    OrderItemRepository orderItemRepository;
    TelegramPublishRepository telegramPublishRepository;
    CustomerRepository customerRepository;
    PaymentRepository paymentRepository;
    PaymentCheckRepository paymentCheckRepository;
    TransactionTemplate transactionTemplate;
    ShopRepository shopRepository;
    ProductRepository productRepository;
    ProductGroupRepository productGroupRepository;
    ProductInventoryRepository productInventoryRepository;
    ProductInventoryTypeRepository productInventoryTypeRepository;

    @GetMapping("findAllActive/{shopId}")
    @Secured({"SYSTEM", "CASHIER", "ADMIN"})
    public List<Order> findAllActive(@PathVariable UUID shopId) {
        return orderRepository.findAllActive(shopId)
            .map(order -> order
                .setCustomer(order.getCustomerId() != null ? customerRepository.findById(order.getCustomerId()).get() : null)
                .setShop(shopRepository.findById(order.getShopId()).get())
                .setCustomerOrderItemList(
                    orderItemRepository.findAllByOrderId(order.getId())
                        .map(i -> i.setProduct(productRepository.findById(i.getProductId()).get()))));
    }

    @Transactional
    @PostMapping("create")
    public UUID create(@RequestBody @NonNull OrderRequest orderRequest) {
        final Order order =
            orderRepository.save(
                new Order()
                    .setCreatedAt(Instant.now())
                    .setCustomerId(orderRequest.getCustomerId())
                    .setShopId(orderRequest.getShopId())
                    .setStatus("CREATED"));

        Customer customer = Option.of(order.getCustomerId())
            .flatMap(c -> customerRepository.findById(order.getCustomerId()))
            .getOrNull();

        List<OrderItem> customerOrderItems = List.ofAll(
            orderItemRepository.saveAll(
                orderRequest.getProductQuantityList()
                    .map(e -> new OrderItem()
                        .setOrderId(order.getId())
                        .setProductId(e.getProductId())
                        .setQuantity(e.getQuantity()))
                    .toList()));

        List<OrderItemTransactionData> orderItemTransactionDataList = customerOrderItems.map(i -> {
            Product product = productRepository.findById(i.getProductId())
                .getOrElseThrow(() -> new IllegalArgumentException(
                    "Product with id " + i.getProductId() + " not found"));

            Map<ProductInventory, ShopInventory> productInventoryShopInventoryMap =
                productInventoryRepository.findByProductId(product.getId())
                    .toMap(productInventory -> productInventory,
                        productInventory -> shopInventoryRepository.findByShopIdAndProductInventoryTypeId(
                                orderRequest.getShopId(),
                                productInventory.getProductInventoryTypeId())
                            .getOrElseThrow(() -> new IllegalArgumentException("Shop Inventory not found for ShopId: "
                                + orderRequest.getShopId() + " and InventoryId: "
                                + productInventory.getProductInventoryTypeId())));

            return new OrderItemTransactionData()
                .setProductName(product.getName())
                .setOrderItem(i)
                .setProduct(product)
                .setProductInventoryToShopInventoryMap(productInventoryShopInventoryMap);
        });

        orderItemTransactionDataList.forEach(transactionData ->
            transactionData.getProductInventoryToShopInventoryMap()
                .forEach((productInventory, shopInventory) -> {
                    int requiredQuantity = productInventory.getQuantity() * transactionData.getOrderItem().getQuantity();
                    int availableQuantity = shopInventory.getQuantity() - shopInventory.getReserved();

                    if (availableQuantity < requiredQuantity) {
                        throw new IllegalStateException("Not enough product inventory in shop for order item: "
                            + transactionData.getOrderItem().getId());
                    }

                    shopInventory.setReserved(shopInventory.getReserved() + requiredQuantity);
                    shopInventoryRepository.save(shopInventory);
                }));

        Map<OrderItem, Product> customerOrderItemsMap =
            customerOrderItems.toMap(item -> Tuple.of(
                item, productRepository.findById(item.getProductId()).get()));

        final int price = customerOrderItemsMap.map(t -> t._1.getQuantity() * t._2.getPrice()).sum().intValue();
        final int payInPoints = customer != null && orderRequest.getUsePoints() ? Math.max(customer.getPoints(), price) : 0;
        final int payInMoney = price - payInPoints;

        if (payInPoints > 0) {
            paymentRepository.save(new Payment()
                .setValue(payInPoints)
                .setPaymentSystemId("POINTS")
                .setOrderId(order.getId()));
        }
        if (payInMoney > 0) {
            paymentRepository.save(new Payment()
                .setValue(payInMoney)
                .setPaymentSystemId("MONEY")
                .setOrderId(order.getId()));
        }

        if (customer != null) {
            Shop shop = shopRepository.findById(order.getShopId()).get();
            telegramPublishRepository.save(new TelegramPublish()
                .setTelegramId(customer.getTelegramId())
                .setText("Оформлен заказ в магазине " + shop.getName() + ", " + shop.getLocation() + ":\n"
                    + orderItemTransactionDataList.map((t) ->
                        t.getProductName()
                            + " - "
                            + t.getOrderItem().getQuantity()
                            + "шт. - "
                            + t.getOrderItem().getQuantity() * t.getProduct().getPrice()
                            + "Р")
                    .mkString("\n")
                    + "\nИтого: " + price + "Р\n"
                    + (payInPoints > 0 ? ("Оплачено баллами: " + payInPoints + "/" + price) : "")));
        }

        return order.getId();
    }

    @Data
    @Accessors(chain = true)
    public static class OrderItemTransactionData {
        String productName;
        OrderItem orderItem;
        Product product;
        Map<ProductInventory, ShopInventory> productInventoryToShopInventoryMap;
    }

    @PostMapping("complete")
    @Secured({"SYSTEM", "CASHIER", "ADMIN"})
    public void orderComplete(Order order) {
        orderRepository.findById(order.getId())
            .map(o -> o.setCompletedAt(Instant.now()))
            .forEach(orderRepository::save);
    }
}
