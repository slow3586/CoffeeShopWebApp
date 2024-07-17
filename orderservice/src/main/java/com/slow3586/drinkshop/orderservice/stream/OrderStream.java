package com.slow3586.drinkshop.orderservice.stream;

import com.slow3586.drinkshop.api.mainservice.entity.Order;
import com.slow3586.drinkshop.api.mainservice.entity.Payment;
import com.slow3586.drinkshop.api.mainservice.topic.OrderTopics;
import com.slow3586.drinkshop.api.mainservice.topic.PaymentTopics;
import com.slow3586.drinkshop.orderservice.repository.OrderItemRepository;
import com.slow3586.drinkshop.orderservice.repository.OrderRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Suppressed;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class OrderStream {
    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    KafkaTemplate<UUID, Object> kafkaTemplate;
    StreamsBuilder streamsBuilder;

    @PostConstruct
    public void orderStreams() {
        JsonSerde<Order> orderSerde = new JsonSerde<>(Order.class);
        JsonSerde<Payment> paymentSerde = new JsonSerde<>(Payment.class);

        KTable<UUID, Order> orderCreatedTable = streamsBuilder.table(
            OrderTopics.Transaction.CREATED,
            Consumed.with(Serdes.UUID(), orderSerde));
        KTable<UUID, Order> orderPaymentTable = streamsBuilder.table(
            OrderTopics.Transaction.PAYMENT,
            Consumed.with(Serdes.UUID(), orderSerde));
        KTable<UUID, Order> orderPaidTable = streamsBuilder.table(
            OrderTopics.Transaction.PAID,
            Consumed.with(Serdes.UUID(), orderSerde));
        KTable<UUID, Order> orderCompleteTable = streamsBuilder.table(
            OrderTopics.Transaction.COMPLETED,
            Consumed.with(Serdes.UUID(), orderSerde));
        KTable<UUID, Order> orderCancelledStatus = streamsBuilder.table(
            OrderTopics.Status.STATUS_CANCELLED,
            Consumed.with(Serdes.UUID(), orderSerde));
        KTable<UUID, Order> orderCompletedStatus = streamsBuilder.table(
            OrderTopics.Status.STATUS_COMPLETED,
            Consumed.with(Serdes.UUID(), orderSerde));
        KTable<UUID, Payment> paidPaymentTable = streamsBuilder.table(PaymentTopics.STATUS_PAID,
            Consumed.with(Serdes.UUID(), paymentSerde));

        // CREATE TIMEOUT
        orderCreatedTable
            .suppress(Suppressed.untilTimeLimit(Duration.ofSeconds(10), null))
            .leftJoin(orderPaymentTable, (a, b) -> b)
            .filter((k, v) -> v == null)
            .mapValues(v -> v.setError("TIMEOUT_CREATE"))
            .toStream()
            .to(OrderTopics.Transaction.ERROR);

        // PAYMENT RECEIVED
        orderPaymentTable
            .join(paidPaymentTable, Order::setPayment)
            .toStream()
            .to(OrderTopics.Transaction.PAID);

        // PAYMENT TIMEOUT
        orderPaymentTable
            .suppress(Suppressed.untilTimeLimit(Duration.ofSeconds(60), null))
            .leftJoin(paidPaymentTable, Order::setPayment)
            .filter((k, v) -> v.getPayment() == null)
            .mapValues(v -> v.setError("TIMEOUT_PAYMENT"))
            .toStream()
            .to(OrderTopics.Transaction.ERROR);

        // COMPLETE RECEIVED
        orderPaymentTable
            .join(orderCompletedStatus, (a, b) -> a)
            .toStream()
            .to(OrderTopics.Transaction.COMPLETED);

        // COMPLETE TIMEOUT
        orderPaidTable
            .suppress(Suppressed.untilTimeLimit(Duration.ofMinutes(30), null))
            .leftJoin(orderCompletedStatus, (a, b) -> a.setCompletedAt(Instant.now()))
            .filter((k, v) -> v.getCompletedAt() == null)
            .mapValues(v -> v.setError("TIMEOUT_COMPLETED"))
            .toStream()
            .to(OrderTopics.Transaction.ERROR);
    }
}
