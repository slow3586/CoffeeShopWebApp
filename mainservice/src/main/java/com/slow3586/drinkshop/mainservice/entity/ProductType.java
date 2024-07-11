package com.slow3586.drinkshop.mainservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table(name = "product_type")
@NoArgsConstructor
@AllArgsConstructor
public class ProductType {
    @Id
    String id;
    String name;
    String color;
    String shopTypeId;

}
