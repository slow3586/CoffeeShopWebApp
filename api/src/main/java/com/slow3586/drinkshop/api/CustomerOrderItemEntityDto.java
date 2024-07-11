package com.slow3586.drinkshop.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link CustomerOrderItemEntity}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CustomerOrderItemEntityDto implements Serializable {
    private UUID id;
    private UUID orderId;
    private UUID productTypeId;
    private int quantity;
    private Instant createdAt;
    private Instant lastModifiedAt;
}