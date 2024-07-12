package com.slow3586.drinkshop.api.mainservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PromoRequest {
    String code;
    String name;
    String text;
    String productTypeId;
    Instant startsAt;
    Instant endsAt;
}
