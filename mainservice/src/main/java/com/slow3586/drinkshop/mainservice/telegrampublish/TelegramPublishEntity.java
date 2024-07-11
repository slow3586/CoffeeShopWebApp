package com.slow3586.drinkshop.mainservice.telegrampublish;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@Entity(name = "telegram_publish")
@NoArgsConstructor
@AllArgsConstructor
public class TelegramPublishEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    UUID id;
    String telegramId;
    @Column(length = 4000)
    String text;
    Instant sentAt;

    @ColumnDefault("0")
    int attempts;
    @Column(length = 4000)
    String error;
    Instant lastAttemptAt;

    @CreationTimestamp Instant createdAt;
    @UpdateTimestamp Instant lastModifiedAt;
}
