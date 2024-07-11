package com.slow3586.drinkshop.mainservice.producttype;

import com.slow3586.drinkshop.mainservice.product.ProductEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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
@Entity(name = "product_type")
@NoArgsConstructor
@AllArgsConstructor
public class ProductTypeEntity {
    @Id
    String id;
    String name;
    String color;
    String shopTypeId;

    @CreationTimestamp Instant createdAt;
    @UpdateTimestamp Instant lastModifiedAt;
}
