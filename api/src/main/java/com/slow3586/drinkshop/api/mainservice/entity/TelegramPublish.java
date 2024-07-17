package com.slow3586.drinkshop.api.mainservice.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.List;
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
    UUID customerId;
    UUID customerGroupId;
    String text;
    String status;
    Instant sentAt;

    @Transient
    List<Customer> customerList;
}
