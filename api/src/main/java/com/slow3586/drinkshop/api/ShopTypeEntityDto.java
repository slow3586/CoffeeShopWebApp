package com.slow3586.drinkshop.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link ShopTypeEntity}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ShopTypeEntityDto implements Serializable {
    private String id;
    private String name;
    private String location;
    private String status;
    private Instant createdAt;
    private Instant lastModifiedAt;
}