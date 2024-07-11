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
 * DTO for {@link TelegramPublishEntity}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class TelegramPublishEntityDto implements Serializable {
    private UUID id;
    private String customerId;
    private String text;
    private Instant createdAt;
    private Instant lastModifiedAt;
}