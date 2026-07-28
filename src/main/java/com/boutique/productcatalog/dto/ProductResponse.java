package com.boutique.productcatalog.dto;

import com.boutique.productcatalog.entity.ProductStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String sku,
        String name,
        String description,
        String category,
        BigDecimal price,
        String currency,
        String imageUrl,
        ProductStatus status,
        Instant createdAt,
        Instant updatedAt,
        Long version
) {
}