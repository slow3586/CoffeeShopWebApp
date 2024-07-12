package com.slow3586.drinkshop.api.mainservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.slow3586.drinkshop.api.mainservice.entity.Customer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PromoTransaction {
    UUID promoId;
    PromoRequest promoRequest;
    List<Customer> validCustomers;
    Boolean registeredForTelegram;
    String declineReason;
}
