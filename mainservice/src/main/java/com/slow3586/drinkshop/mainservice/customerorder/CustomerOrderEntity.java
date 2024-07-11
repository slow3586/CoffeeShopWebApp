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
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@Entity(name = "customer_order")
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
    @CreationTimestamp Instant createdAt;
    @UpdateTimestamp Instant lastModifiedAt;
}
