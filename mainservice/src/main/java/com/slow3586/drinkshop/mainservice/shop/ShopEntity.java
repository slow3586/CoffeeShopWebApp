package com.slow3586.drinkshop.mainservice.shop;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Entity(name = "shop")
@NoArgsConstructor
@AllArgsConstructor
public class ShopEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    String shopTypeId;
    String name;
    String location;
    String status;
    @CreationTimestamp Instant createdAt;
    @UpdateTimestamp Instant lastModifiedAt;
}
