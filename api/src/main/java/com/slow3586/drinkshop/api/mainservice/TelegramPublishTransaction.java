package com.slow3586.drinkshop.api.mainservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.slow3586.drinkshop.api.mainservice.entity.Customer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TelegramPublishTransaction {
    Customer customer;
    String text;
    Boolean sent;
}
