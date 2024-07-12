package com.slow3586.drinkshop.api.mainservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OrderRequest {
    UUID customerId;
    UUID shopId;
    UUID productId;
    int quantity;
}
