package com.slow3586.drinkshop.mainservice.entity;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@Table(name = "customer")
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    @Id
    UUID id;
    @NotNull
    String telegramId;
    String name;
    double points;
    String phoneNumber;
    String qrCode;
    Instant qrCodeExpiresAt;
    String blockedReason;
}
