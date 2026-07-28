package com.boutique.productcatalog.controller;

import com.boutique.productcatalog.dto.CreateProductRequest;
import com.boutique.productcatalog.dto.ProductResponse;
import com.boutique.productcatalog.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@Tag(
        name = "Products",
        description = "Product catalogue management APIs"
)
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @Operation(
            summary = "Create a product",
            description = "Creates a new active product in the catalogue."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "SKU already exists")
    })
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request
    ) {
        ProductResponse createdProduct =
                productService.createProduct(request);

        URI location = URI.create(
                "/api/v1/products/" + createdProduct.id()
        );

        return ResponseEntity
                .created(location)
                .body(createdProduct);
    }

    @GetMapping
    @Operation(
            summary = "Get all products",
            description = "Returns all products currently stored in the catalogue."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Products returned successfully"
    )
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(
                productService.getAllProducts()
        );
    }

    @GetMapping("/{productId}")
    @Operation(
            summary = "Get product by ID",
            description = "Returns one product using its UUID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "400", description = "Invalid UUID"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable UUID productId
    ) {
        return ResponseEntity.ok(
                productService.getProductById(productId)
        );
    }
}