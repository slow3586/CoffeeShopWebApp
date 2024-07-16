package com.slow3586.drinkshop.api.mainservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@Accessors(chain = true)
@Table(name = "worker")
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Worker {
    @Id
    UUID id;
    String name;
    String status;
    String telegramId;

    String phoneNumber;
    String role;
    @JsonIgnore
    @ToString.Exclude
    String password;

    String blockedReason;

    String qrCode;
    Instant qrCodeExpiresAt;
}
