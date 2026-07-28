package com.boutique.productcatalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateProductRequest(

        @NotBlank(message = "SKU is required")
        @Size(max = 100, message = "SKU must not exceed 100 characters")
        String sku,

        @NotBlank(message = "Product name is required")
        @Size(max = 200, message = "Product name must not exceed 200 characters")
        String name,

        @NotBlank(message = "Description is required")
        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        String description,

        @NotBlank(message = "Category is required")
        @Size(max = 100, message = "Category must not exceed 100 characters")
        String category,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.00", message = "Price must not be negative")
        BigDecimal price,

        @NotBlank(message = "Currency is required")
        @Size(min = 3, max = 3, message = "Currency must contain exactly 3 characters")
        String currency,

        @Size(max = 1000, message = "Image URL must not exceed 1000 characters")
        String imageUrl
) {
}