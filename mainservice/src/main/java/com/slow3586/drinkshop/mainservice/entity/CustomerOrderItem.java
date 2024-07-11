package com.slow3586.drinkshop.mainservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Builder
@Table(name = "customer_order_item")
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrderItem {
    @Id
    UUID id;
    UUID orderId;
    UUID productTypeId;
    int quantity;
}
