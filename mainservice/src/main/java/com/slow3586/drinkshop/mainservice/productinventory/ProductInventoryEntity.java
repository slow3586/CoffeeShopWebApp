package com.slow3586.drinkshop.mainservice.productinventory;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@Entity
@Table("product_inventory")
@NoArgsConstructor
@AllArgsConstructor
public class ProductInventoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    UUID productId;
    UUID inventoryId;
    double quantity;
    @CreatedDate Instant createdAt;
    @LastModifiedDate Instant lastModifiedAt;
}
