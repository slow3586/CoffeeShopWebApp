package com.slow3586.drinkshop.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link InventoryTypeEntity}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class InventoryTypeEntityDto implements Serializable {
    private String id;
    private String name;
    private Instant createdAt;
    private Instant lastModifiedAt;
}