package com.boutique.productcatalog.service;

import com.boutique.productcatalog.dto.CreateProductRequest;
import com.boutique.productcatalog.dto.ProductPageResponse;
import com.boutique.productcatalog.dto.ProductResponse;
import com.boutique.productcatalog.entity.Product;
import com.boutique.productcatalog.entity.ProductStatus;
import com.boutique.productcatalog.exception.DuplicateSkuException;
import com.boutique.productcatalog.exception.ProductNotFoundException;
import com.boutique.productcatalog.mapper.ProductMapper;
import com.boutique.productcatalog.repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProductService {

    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    /*
     * Exact totals are useful to the current frontend, but computing COUNT(*)
     * on every hot browse/search request was the major DB amplification seen
     * during load testing.
     *
     * Product catalogue counts are slow-changing metadata, so cache only the
     * COUNT result for a short period. Content itself is NEVER cached here.
     *
     * This is deliberately bounded and dependency-free:
     * - 30 second TTL
     * - maximum 512 filter keys
     * - invalidated immediately when this service creates a product
     *
     * In AWS a shared cache may later replace this if exact cross-replica
     * count freshness becomes a requirement.
     */
    private static final Duration COUNT_TTL = Duration.ofSeconds(30);
    private static final int MAX_COUNT_KEYS = 512;

    private final ProductRepository repository;
    private final ProductMapper mapper;
    private final Map<CountKey, CachedCount> countCache = new ConcurrentHashMap<>();

    public ProductService(ProductRepository repository, ProductMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        String sku = request.sku().trim();

        if (repository.existsBySkuIgnoreCase(sku)) {
            throw new DuplicateSkuException(sku);
        }

        Product saved = repository.save(mapper.toEntity(request));

        // Catalogue cardinality changed. Never keep stale totals after a local write.
        countCache.clear();

        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ProductPageResponse searchProducts(
            String rawQuery,
            String rawCategory,
            int rawPage,
            int rawSize
    ) {
        String query = normalize(rawQuery);
        String category = normalize(rawCategory);

        int page = Math.max(0, rawPage);
        int size = Math.max(
                1,
                Math.min(
                        rawSize <= 0 ? DEFAULT_PAGE_SIZE : rawSize,
                        MAX_PAGE_SIZE
                )
        );

        var pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Slice<Product> products;

        if (query == null && category == null) {
            products = repository.findByStatus(
                    ProductStatus.ACTIVE,
                    pageable
            );
        } else if (query == null) {
            products = repository.findByStatusAndCategoryIgnoreCase(
                    ProductStatus.ACTIVE,
                    category,
                    pageable
            );
        } else if (category == null) {
            products = repository.search(
                    ProductStatus.ACTIVE,
                    query,
                    pageable
            );
        } else {
            products = repository.searchInCategory(
                    ProductStatus.ACTIVE,
                    query,
                    category,
                    pageable
            );
        }

        long totalElements = exactCount(query, category);
        int totalPages = totalElements == 0
                ? 0
                : Math.toIntExact(
                        Math.min(
                                Integer.MAX_VALUE,
                                (totalElements + size - 1L) / size
                        )
                );

        return new ProductPageResponse(
                products.getContent()
                        .stream()
                        .map(mapper::toResponse)
                        .toList(),
                page,
                size,
                totalElements,
                totalPages,
                page == 0,
                !products.hasNext()
        );
    }

    @Transactional(readOnly = true)
    public ProductPageResponse getProducts(int page, int size) {
        return searchProducts(null, null, page, size);
    }

    @Transactional(readOnly = true)
    public List<String> getCategories() {
        return repository.findActiveCategories();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new ProductNotFoundException(id);
        }

        return mapper.toResponse(product);
    }

    private long exactCount(String query, String category) {
        CountKey key = new CountKey(query, category);
        Instant now = Instant.now();

        CachedCount current = countCache.get(key);
        if (current != null && now.isBefore(current.expiresAt())) {
            return current.value();
        }

        long value;

        if (query == null && category == null) {
            value = repository.countByStatus(ProductStatus.ACTIVE);
        } else if (query == null) {
            value = repository.countByStatusAndCategoryIgnoreCase(
                    ProductStatus.ACTIVE,
                    category
            );
        } else if (category == null) {
            value = repository.countSearch(
                    ProductStatus.ACTIVE,
                    query
            );
        } else {
            value = repository.countSearchInCategory(
                    ProductStatus.ACTIVE,
                    query,
                    category
            );
        }

        /*
         * Keep the map bounded without adding another runtime technology.
         * Eviction is metadata-only and does not affect correctness.
         */
        if (countCache.size() >= MAX_COUNT_KEYS && !countCache.containsKey(key)) {
            countCache.entrySet()
                    .stream()
                    .min(Comparator.comparing(e -> e.getValue().expiresAt()))
                    .ifPresent(entry -> countCache.remove(entry.getKey()));
        }

        countCache.put(
                key,
                new CachedCount(value, now.plus(COUNT_TTL))
        );

        return value;
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value
                .trim()
                .toLowerCase(Locale.ROOT);

        return normalized.isEmpty()
                ? null
                : normalized;
    }

    private record CountKey(
            String query,
            String category
    ) {}

    private record CachedCount(
            long value,
            Instant expiresAt
    ) {}
}
