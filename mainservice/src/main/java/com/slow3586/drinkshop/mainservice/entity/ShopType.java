package com.slow3586.drinkshop.mainservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table(name = "shop_type")
@NoArgsConstructor
@AllArgsConstructor
public class ShopType {
    @Id
    String id;
    String name;
    String location;
    String status;
}
