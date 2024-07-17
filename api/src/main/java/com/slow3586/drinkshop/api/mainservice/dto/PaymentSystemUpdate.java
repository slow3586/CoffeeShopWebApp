package com.slow3586.drinkshop.api.mainservice.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain = true)
public class PaymentSystemUpdate {
    UUID orderId;
    String status;
    String checkId;
}
