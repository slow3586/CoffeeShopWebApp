package com.slow3586.drinkshop.mainservice.customerorderitem;

import com.slow3586.drinkshop.mainservice.customerorder.CustomerOrderEntity;
import com.slow3586.drinkshop.mainservice.producttype.ProductTypeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@Entity(name = "customer_order_item")
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    UUID orderId;
    UUID productTypeId;
    int quantity;
    @CreationTimestamp Instant createdAt;
    @UpdateTimestamp Instant lastModifiedAt;
}
