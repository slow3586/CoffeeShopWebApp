package com.slow3586.drinkshop.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link ShopShiftEntity}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ShopShiftEntityDto implements Serializable {
    private UUID id;
    private UUID shopId;
    private UUID workerId;
    private Instant createdAt;
    private Instant lastModifiedAt;
}