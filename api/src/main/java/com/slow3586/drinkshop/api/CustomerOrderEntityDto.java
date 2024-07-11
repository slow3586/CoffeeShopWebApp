package com.slow3586.drinkshop.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link CustomerOrderEntity}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class CustomerOrderEntityDto implements Serializable {
    private UUID id;
    private UUID customerId;
    private UUID shopId;
    private String status;
    private int rating;
    private Instant createdAt;
    private Instant lastModifiedAt;
}