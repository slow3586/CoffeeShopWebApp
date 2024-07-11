package com.slow3586.drinkshop.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link PromoEntity}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PromoEntityDto implements Serializable {
    private UUID id;
    private String code;
    private String name;
    private String shopTypeId;
    private String productTypeId;
    private String status;
    private Instant createdAt;
    private Instant lastModifiedAt;
}