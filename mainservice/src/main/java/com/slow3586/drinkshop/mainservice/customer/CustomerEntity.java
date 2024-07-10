package com.slow3586.drinkshop.mainservice.customer;

import com.slow3586.drinkshop.mainservice.customerorder.CustomerOrderEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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
@Table("customer")
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    String telegramId;
    String name;
    double points;
    String phoneNumber;
    String qrCode;
    Instant qrCodeExpiresAt;
    String blockedReason;
    @CreatedDate Instant createdAt;
    @LastModifiedDate Instant lastModifiedAt;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    List<CustomerOrderEntity> orderList;
}
