package com.boutique.productcatalog.mapper;

import com.boutique.productcatalog.dto.CreateProductRequest;
import com.boutique.productcatalog.dto.ProductResponse;
import com.boutique.productcatalog.entity.Product;
import com.boutique.productcatalog.entity.ProductStatus;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(CreateProductRequest request) {
        return Product.builder()
                .sku(request.sku().trim().toUpperCase())
                .name(request.name().trim())
                .description(request.description().trim())
                .category(request.category().trim().toLowerCase())
                .price(request.price())
                .currency(request.currency().trim().toUpperCase())
                .imageUrl(normalizeOptionalText(request.imageUrl()))
                .status(ProductStatus.ACTIVE)
                .build();
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getPrice(),
                product.getCurrency(),
                product.getImageUrl(),
                product.getStatus(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getVersion()
        );
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}