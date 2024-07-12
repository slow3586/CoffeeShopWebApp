package com.slow3586.drinkshop.api.mainservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain = true, fluent = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TelegramPublishRequest {
    UUID customerId;
    String text;
}
