package com.slow3586.drinkshop.mainservice.shoptype;

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
@Table("shop")
@NoArgsConstructor
@AllArgsConstructor
public class ShopTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    String name;
    String location;
    String status;
    @CreatedDate Instant createdAt;
    @LastModifiedDate Instant lastModifiedAt;
}
