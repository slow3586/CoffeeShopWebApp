package com.slow3586.drinkshop.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link ProductEntity}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ProductEntityDto implements Serializable {
    private String id;
    private String productTypeId;
    private String label;
    private double price;
    private Instant createdAt;
    private Instant lastModifiedAt;
}