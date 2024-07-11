package com.slow3586.drinkshop.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link ShopInventoryEntity}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ShopInventoryEntityDto implements Serializable {
    private UUID id;
    private UUID inventoryTypeId;
    private UUID shopId;
    private Instant createdAt;
    private Instant lastModifiedAt;
}