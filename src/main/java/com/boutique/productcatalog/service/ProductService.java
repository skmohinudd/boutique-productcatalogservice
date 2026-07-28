package com.boutique.productcatalog.service;

import com.boutique.productcatalog.dto.CreateProductRequest;
import com.boutique.productcatalog.dto.ProductResponse;
import com.boutique.productcatalog.entity.Product;
import com.boutique.productcatalog.exception.DuplicateSkuException;
import com.boutique.productcatalog.exception.ProductNotFoundException;
import com.boutique.productcatalog.mapper.ProductMapper;
import com.boutique.productcatalog.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(
            ProductRepository productRepository,
            ProductMapper productMapper
    ) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        String normalizedSku = request.sku().trim();

        if (productRepository.existsBySkuIgnoreCase(normalizedSku)) {
            throw new DuplicateSkuException(normalizedSku);
        }

        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        return productMapper.toResponse(product);
    }
}