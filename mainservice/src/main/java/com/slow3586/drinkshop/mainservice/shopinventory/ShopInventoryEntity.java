package com.slow3586.drinkshop.mainservice.shopinventory;

import com.slow3586.drinkshop.mainservice.inventorytype.InventoryTypeEntity;
import com.slow3586.drinkshop.mainservice.shop.ShopEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table("shopinventory")
@NoArgsConstructor
@AllArgsConstructor
public class ShopInventoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    UUID inventoryTypeId;
    UUID shopId;
    @CreatedDate Instant createdAt;
    @LastModifiedDate Instant lastModifiedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    ShopEntity shop;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_type_id")
    InventoryTypeEntity inventoryType;
}
