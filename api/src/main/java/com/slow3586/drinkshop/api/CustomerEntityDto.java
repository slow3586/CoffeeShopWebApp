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
 * DTO for {@link CustomerEntity}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class CustomerEntityDto implements Serializable {
    private UUID id;
    private String telegramId;
    private String name;
    private double points;
    private String phoneNumber;
    private String qrCode;
    private Instant qrCodeExpiresAt;
    private String blockedReason;
    private Instant createdAt;
    private Instant lastModifiedAt;
}