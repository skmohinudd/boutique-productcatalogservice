package com.boutique.productcatalog.repository;

import com.boutique.productcatalog.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    boolean existsBySkuIgnoreCase(String sku);
}