package com.slow3586.drinkshop.mainservice.service;

import com.slow3586.drinkshop.api.mainservice.OrderTransaction;
import com.slow3586.drinkshop.mainservice.repository.ShopInventoryRepository;
import com.slow3586.drinkshop.mainservice.repository.ShopRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/shop")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ShopService {
    ShopRepository shopRepository;
    ShopInventoryRepository shopInventoryRepository;
    StreamsBuilder streamsBuilder;
    KafkaTemplate<UUID, Object> kafkaTemplate;

    @PostConstruct
    public void createOrderStream() {
        JsonSerde<OrderTransaction> createOrderOperationJsonSerde = new JsonSerde<>();
        createOrderOperationJsonSerde.deserializer().trustedPackages("*");

        // RESERVE INVENTORY
        KStream<String, OrderTransaction> stream = streamsBuilder
            .stream("order_transaction", Consumed.with(Serdes.String(), createOrderOperationJsonSerde));

        // JOIN SHOP INVENTORY
        stream.filter((k, op) -> op.getCancelledReason() == null)
            .filter((k, op) -> op.getProductInventoryList() != null && op.getShopInventoryList() == null)
            .mapValues((k, operation) -> operation.setShopInventoryList(
                operation.getProductInventoryList().stream()
                    .map(p -> shopInventoryRepository
                        .findByShopIdAndInventoryTypeId(
                            operation.getOrderRequest().getShopId(),
                            p.getInventoryId())
                    ).toList()))
            .to("order_transaction");

        // RESERVE
        stream.filter((k, op) -> op.getCancelledReason() == null)
            .filter((k, op) -> op.getShopInventoryList() != null && op.getInventoryReserved() == null)
            .mapValues((k, operation) -> {
                try {
                    operation.getShopInventoryList().forEach(i ->
                        shopInventoryRepository
                            .findById(i.getId())
                            .ifPresent(shopInventory -> {
                                double toReserve = operation.getProductInventoryList()
                                    .stream()
                                    .filter(productInventory -> productInventory
                                        .getInventoryId()
                                        .equals(shopInventory.getId()))
                                    .findFirst()
                                    .orElseThrow()
                                    .getQuantity();
                                double newReserved = shopInventory.getReserved() + toReserve;
                                if (shopInventory.getQuantity() - newReserved < 0) {
                                    throw new IllegalStateException("Reserved amount exceeded");
                                }
                                shopInventory.setReserved(newReserved);
                                shopInventoryRepository.save(shopInventory);
                            }));
                } catch (Exception e) {
                    operation.setInventoryReserved(false);
                }
                return operation.setInventoryReserved(true);
            }).to("order_transaction");

        // RESERVE INVENTORY RESET
        stream.filter((k, op) -> op.getInventoryReserved() == Boolean.TRUE && op.getCancelledReason() != null)
            .foreach((k, operation) -> operation.getShopInventoryList()
                .forEach(shopInventory ->
                    shopInventoryRepository
                        .findById(shopInventory.getId())
                        .ifPresent(shopInventoryCurrent -> {
                            double toReserve = operation.getProductInventoryList()
                                .stream()
                                .filter(p -> p.getInventoryId().equals(shopInventoryCurrent.getId()))
                                .findFirst()
                                .orElseThrow()
                                .getQuantity();
                            shopInventoryCurrent.setReserved(shopInventoryCurrent.getReserved() - toReserve);
                            shopInventoryRepository.save(shopInventoryCurrent);
                        })));
    }
}
