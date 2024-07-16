package com.slow3586.drinkshop.api.mainservice.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@Accessors(chain = true)
@Table(name = "telegram_publish")
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TelegramPublish {
    @Id
    UUID id;
    String telegramBotId;
    String telegramId;
    String text;
    Instant sentAt;
    int attempts;
    String error;
    Instant lastAttemptAt;
}
