package com.slow3586.drinkshop.api.mainservice.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
@Table(name = "customer_order")
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Order {
    @Id
    UUID id;
    UUID customerId;
    UUID shopId;

    String status;

    Instant createdAt;
    Instant paidAt;
    Instant completedAt;

    boolean usePoints;

    @Transient
    Customer customer;
    @Transient
    Shop shop;
    @Transient
    List<OrderItem> orderItemList;
    @Transient
    Payment paymentMoney;
    @Transient
    Payment paymentPoints;
}