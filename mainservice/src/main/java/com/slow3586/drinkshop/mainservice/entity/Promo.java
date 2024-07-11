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
@Table(name = "promo")
@NoArgsConstructor
@AllArgsConstructor
public class Promo {
    @Id
    UUID id;
    String code;
    @NotNull
    String name;
    @NotNull
    String text;
    byte[] image;
    String productTypeId;
    @NotNull
    Instant startsAt;
    @NotNull
    Instant endsAt;
}
