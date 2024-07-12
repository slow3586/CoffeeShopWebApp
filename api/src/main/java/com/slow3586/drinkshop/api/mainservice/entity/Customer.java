package com.slow3586.drinkshop.api.mainservice.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@Accessors(chain = true, fluent = true)
@Table(name = "customer")
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
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
