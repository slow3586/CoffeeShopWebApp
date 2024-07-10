package com.slow3586.drinkshop.api;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CustomerDto {
    UUID id;
    String telegramId;
    String name;
    double points;
    String phoneNumber;
}
