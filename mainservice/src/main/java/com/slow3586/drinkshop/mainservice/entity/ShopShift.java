package com.slow3586.drinkshop.mainservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Builder
@Table(name = "shop_shift")
@NoArgsConstructor
@AllArgsConstructor
public class ShopShift {
    @Id
    UUID id;
    UUID shopId;
    UUID workerId;
}
