package com.slow3586.drinkshop.api.mainservice.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.UUID;

@Data
@Accessors(chain = true)
@Table(name = "promo")
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Validated
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
    Boolean queuedForTelegram;
    @NotNull
    Instant startsAt;
    @NotNull
    Instant endsAt;

    @Transient
    TelegramPublish telegramPublish;
}
