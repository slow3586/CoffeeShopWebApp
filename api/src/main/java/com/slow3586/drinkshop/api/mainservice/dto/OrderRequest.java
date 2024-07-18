package com.slow3586.drinkshop.api.mainservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OrderRequest {
    UUID customerId;
    @NotNull
    UUID shopId;
    boolean usePoints;
    @Size(min = 1, max = 10)
    List<OrderRequestItem> orderRequestItemList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class OrderRequestItem {
        @NotNull
        UUID productId;
        @Min(1)
        @Max(10)
        int quantity;
    }
}
