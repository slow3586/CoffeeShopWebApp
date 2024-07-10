package com.slow3586.drinkshop.mainservice.product;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("product")
public class ProductEntity {
    @Id String id;
    String name;
    double price;
    String color;
}
