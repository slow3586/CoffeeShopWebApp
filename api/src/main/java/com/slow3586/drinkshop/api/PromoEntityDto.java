package com.slow3586.drinkshop.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PromoEntityDto implements Serializable {
    UUID id;
    String code;
    String name;
    String text;
    byte[] image;
    String shopTypeId;
    String productTypeId;
    String status;
    Instant startsAt;
    Instant endsAt;
    Instant createdAt;
    Instant lastModifiedAt;
}