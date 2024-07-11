package com.slow3586.drinkshop.mainservice.product;

import com.slow3586.drinkshop.mainservice.productinventory.ProductInventoryEntity;
import com.slow3586.drinkshop.mainservice.producttype.ProductTypeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@Entity(name = "product")
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {
    @Id
    String id;
    String productTypeId;
    String label;
    double price;
    @CreationTimestamp Instant createdAt;
    @UpdateTimestamp Instant lastModifiedAt;
}
