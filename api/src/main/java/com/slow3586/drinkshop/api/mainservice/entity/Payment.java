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
@Table(name = "payment")
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Payment {
    @Id
    UUID id;
    UUID orderId;


    String status;
    int value;
    int points;

    Instant sentToPaymentSystemAt;

    String checkId;
    Instant checkReceivedAt;
    String checkNote;
}
