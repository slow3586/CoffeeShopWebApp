package com.slow3586.drinkshop.api.mainservice;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain = true)
public class PaymentSystemUpdate {
    UUID paymentId;
    String status;
}
