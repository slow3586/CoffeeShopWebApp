package com.slow3586.drinkshop.mainservice.customerorder;

import com.slow3586.drinkshop.mainservice.customer.CustomerEntity;
import com.slow3586.drinkshop.mainservice.productinventory.ProductInventoryEntity;
import com.slow3586.drinkshop.mainservice.shop.ShopEntity;
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
@Table("order")
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    UUID customerId;
    UUID shopId;
    String status;
    int rating;
    @CreatedDate Instant createdAt;
    @LastModifiedDate Instant lastModifiedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    CustomerEntity customerEntity;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    ShopEntity shopEntity;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    List<ProductInventoryEntity> productInventoryList;
}
