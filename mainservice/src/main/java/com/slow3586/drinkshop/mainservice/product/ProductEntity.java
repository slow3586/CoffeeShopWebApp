package com.slow3586.drinkshop.mainservice.product;

import com.slow3586.drinkshop.mainservice.productinventory.ProductInventoryEntity;
import com.slow3586.drinkshop.mainservice.producttype.ProductTypeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity
@Table("product")
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    UUID productTypeId;
    String label;
    double price;
    @CreatedDate Instant createdAt;
    @LastModifiedDate Instant lastModifiedAt;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_type_id")
    ProductTypeEntity productType;
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    List<ProductInventoryEntity> productInventoryList;
}
