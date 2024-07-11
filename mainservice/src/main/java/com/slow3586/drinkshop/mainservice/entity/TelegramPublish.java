package com.slow3586.drinkshop.mainservice.entity;

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
@Table(name = "telegram_publish")
@NoArgsConstructor
@AllArgsConstructor
public class TelegramPublish {
    @Id
    UUID id;
    String telegramId;
    String text;
    Instant sentAt;
    int attempts;
    String error;
    Instant lastAttemptAt;

}
