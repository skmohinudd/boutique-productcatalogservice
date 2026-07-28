package com.boutique.productcatalog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "products",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_products_sku", columnNames = "sku")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String sku;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = ProductStatus.ACTIVE;
        }

        if (currency != null) {
            currency = currency.toUpperCase();
        }
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();

        if (currency != null) {
            currency = currency.toUpperCase();
        }
    }
}