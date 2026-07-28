package com.boutique.productcatalog.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(UUID productId) {
        super("Product with ID '" + productId + "' was not found");
    }
}