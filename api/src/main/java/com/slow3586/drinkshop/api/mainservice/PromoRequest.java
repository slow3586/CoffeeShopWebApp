package com.slow3586.drinkshop.api.mainservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PromoRequest {
    @NotBlank
    String code;
    @NotBlank
    String name;
    @NotBlank
    String text;
    String productTypeId;
    byte[] image;
    Instant startsAt;
    Instant endsAt;
}
