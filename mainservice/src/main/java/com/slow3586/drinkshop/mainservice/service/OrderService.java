package com.slow3586.drinkshop.mainservice.service;

import com.slow3586.drinkshop.api.mainservice.OrderRequest;
import com.slow3586.drinkshop.api.mainservice.entity.Customer;
import com.slow3586.drinkshop.api.mainservice.entity.CustomerOrder;
import com.slow3586.drinkshop.api.mainservice.entity.CustomerOrderItem;
import com.slow3586.drinkshop.api.mainservice.entity.Payment;
import com.slow3586.drinkshop.api.mainservice.entity.Product;
import com.slow3586.drinkshop.api.mainservice.entity.ProductInventory;
import com.slow3586.drinkshop.api.mainservice.entity.ProductType;
import com.slow3586.drinkshop.api.mainservice.entity.Shop;
import com.slow3586.drinkshop.api.mainservice.entity.ShopInventory;
import com.slow3586.drinkshop.api.mainservice.entity.TelegramPublish;
import com.slow3586.drinkshop.mainservice.repository.CustomerOrderItemRepository;
import com.slow3586.drinkshop.mainservice.repository.CustomerOrderRepository;
import com.slow3586.drinkshop.mainservice.repository.CustomerRepository;
import com.slow3586.drinkshop.mainservice.repository.PaymentCheckRepository;
import com.slow3586.drinkshop.mainservice.repository.PaymentRepository;
import com.slow3586.drinkshop.mainservice.repository.ProductInventoryRepository;
import com.slow3586.drinkshop.mainservice.repository.ProductRepository;
import com.slow3586.drinkshop.mainservice.repository.ProductTypeRepository;
import com.slow3586.drinkshop.mainservice.repository.ShopInventoryRepository;
import com.slow3586.drinkshop.mainservice.repository.ShopRepository;
import com.slow3586.drinkshop.mainservice.repository.TelegramPublishRepository;
import io.vavr.Tuple;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import jakarta.ws.rs.QueryParam;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

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
    PaymentRepository paymentRepository;
    PaymentCheckRepository paymentCheckRepository;
    TransactionTemplate transactionTemplate;
    ShopRepository shopRepository;
    ProductTypeRepository productTypeRepository;

    @GetMapping("query")
    public List<CustomerOrder> query(@QueryParam("page") Integer page) {
        return customerOrderRepository.query(
            Optional.ofNullable(page).orElse(0) * 10);
    }

    @Transactional
    @PostMapping("create")
    public UUID create(@RequestBody @NonNull OrderRequest orderRequest) {
        final CustomerOrder customerOrder =
            customerOrderRepository.save(
                new CustomerOrder()
                    .setCreatedAt(Instant.now())
                    .setCustomerId(orderRequest.getCustomerId())
                    .setShopId(orderRequest.getShopId())
                    .setStatus("CREATED"));

        Customer customer = customerRepository.findById(customerOrder.getCustomerId()).orElseThrow();
        Shop shop = shopRepository.findById(customerOrder.getShopId()).get();

        List<CustomerOrderItem> customerOrderItems = List.ofAll(
            customerOrderItemRepository.saveAll(
                orderRequest.getProductQuantityList()
                    .map(e -> new CustomerOrderItem()
                        .setOrderId(customerOrder.getId())
                        .setProductId(e.getProductId())
                        .setQuantity(e.getQuantity()))
                    .toList()));

        List<OrderItemTransactionData> orderItemTransactionDataList = customerOrderItems.map(i -> {
            Product product = productRepository.findById(i.getProductId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Product with id " + i.getProductId() + " not found"));
            ProductType productType = productTypeRepository.findById(product.getProductTypeId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Product type with id " + product.getProductTypeId() + " not found"));

            Map<ProductInventory, ShopInventory> productInventoryShopInventoryMap =
                productInventoryRepository.findByProductId(product.getId())
                    .toMap(productInventory -> productInventory,
                        productInventory -> shopInventoryRepository.findByShopIdAndInventoryTypeId(
                                orderRequest.getShopId(),
                                productInventory.getInventoryId())
                            .getOrElseThrow(() -> new IllegalArgumentException("Shop Inventory not found for ShopId: "
                                + orderRequest.getShopId() + " and InventoryId: "
                                + productInventory.getInventoryId())));

            return new OrderItemTransactionData()
                .setProductName(productType.getName() + " " + product.getLabel())
                .setCustomerOrderItem(i)
                .setProduct(product)
                .setProductType(productType)
                .setProductInventoryToShopInventoryMap(productInventoryShopInventoryMap);
        });

        orderItemTransactionDataList.forEach(transactionData ->
            transactionData.getProductInventoryToShopInventoryMap()
                .forEach((productInventory, shopInventory) -> {
                    int requiredQuantity = productInventory.getQuantity() * transactionData.getCustomerOrderItem().getQuantity();
                    int availableQuantity = shopInventory.getQuantity() - shopInventory.getReserved();

                    if (availableQuantity < requiredQuantity) {
                        throw new IllegalStateException("Not enough product inventory in shop for order item: "
                            + transactionData.getCustomerOrderItem().getId());
                    }

                    shopInventory.setReserved(shopInventory.getReserved() + requiredQuantity);
                    shopInventoryRepository.save(shopInventory);
                }));

        Map<CustomerOrderItem, Product> customerOrderItemsMap =
            customerOrderItems.toMap(item -> Tuple.of(
                item, productRepository.findById(item.getProductId()).get()));

        final int price = customerOrderItemsMap.map(t -> t._1.getQuantity() * t._2.getPrice()).sum().intValue();
        final int payInPoints = orderRequest.getUsePoints() ? Math.max(customer.getPoints(), price) : 0;
        final int payInMoney = price - payInPoints;

        if (payInPoints > 0) {
            paymentRepository.save(new Payment()
                .setValue(payInPoints)
                .setPaymentSystemId("POINTS")
                .setOrderId(customerOrder.getId()));
        }
        if (payInMoney > 0) {
            paymentRepository.save(new Payment()
                .setValue(payInMoney)
                .setPaymentSystemId("MONEY")
                .setOrderId(customerOrder.getId()));
        }

        telegramPublishRepository.save(new TelegramPublish()
            .setTelegramId(customer.getTelegramId())
            .setText("Оформлен заказ в магазине " + shop.getName() + ", " + shop.getLocation() + ":\n"
                + orderItemTransactionDataList.map((t) ->
                    t.getProductName()
                        + " - "
                        + t.getCustomerOrderItem().getQuantity()
                        + "шт. - "
                        + t.getCustomerOrderItem().getQuantity() * t.getProduct().getPrice()
                        + "Р")
                .mkString("\n")
                + "\nИтого: " + price + "Р\n"
                + (payInPoints > 0 ? ("Оплачено баллами: " + payInPoints + "/" + price) : "")));

        return customerOrder.getId();
    }

    @Data
    @Accessors(chain = true)
    public static class OrderItemTransactionData {
        String productName;
        CustomerOrderItem customerOrderItem;
        Product product;
        ProductType productType;
        Map<ProductInventory, ShopInventory> productInventoryToShopInventoryMap;
    }

    @PostMapping("complete")
    public void orderComplete(CustomerOrder order) {
        customerOrderRepository.findById(order.getId())
            .map(o -> o.setCompletedAt(Instant.now()))
            .ifPresent(customerOrderRepository::save);
    }
}
