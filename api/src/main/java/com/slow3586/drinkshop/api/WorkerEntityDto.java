package com.slow3586.drinkshop.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link WorkerEntity}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class WorkerEntityDto implements Serializable {
    private UUID id;
    private String name;
    private String status;
    private Instant createdAt;
    private Instant lastModifiedAt;
}