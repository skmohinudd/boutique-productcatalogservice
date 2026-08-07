package com.boutique.productcatalog.repository;

import com.boutique.productcatalog.entity.Product;
import com.boutique.productcatalog.entity.ProductStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    boolean existsBySkuIgnoreCase(String sku);

    /*
     * IMPORTANT:
     * Slice is intentional.
     *
     * Spring Data Page performs a second COUNT query for every request.
     * Under the final 1000-VU test that caused extreme tuple work and Product
     * Hikari saturation. Slice requests one extra row to determine hasNext()
     * and avoids COUNT(*) on the hot content path.
     */
    Slice<Product> findByStatus(ProductStatus status, Pageable pageable);

    Slice<Product> findByStatusAndCategoryIgnoreCase(
            ProductStatus status,
            String category,
            Pageable pageable
    );

    @Query("""
        select p from Product p
        where p.status = :status
          and (
               lower(p.name) like concat('%', :query, '%')
            or lower(p.description) like concat('%', :query, '%')
            or lower(p.sku) like concat('%', :query, '%')
            or lower(p.category) like concat('%', :query, '%')
          )
        """)
    Slice<Product> search(
            @Param("status") ProductStatus status,
            @Param("query") String query,
            Pageable pageable
    );

    @Query("""
        select p from Product p
        where p.status = :status
          and lower(p.category) = :category
          and (
               lower(p.name) like concat('%', :query, '%')
            or lower(p.description) like concat('%', :query, '%')
            or lower(p.sku) like concat('%', :query, '%')
          )
        """)
    Slice<Product> searchInCategory(
            @Param("status") ProductStatus status,
            @Param("query") String query,
            @Param("category") String category,
            Pageable pageable
    );

    long countByStatus(ProductStatus status);

    long countByStatusAndCategoryIgnoreCase(
            ProductStatus status,
            String category
    );

    @Query("""
        select count(p.id) from Product p
        where p.status = :status
          and (
               lower(p.name) like concat('%', :query, '%')
            or lower(p.description) like concat('%', :query, '%')
            or lower(p.sku) like concat('%', :query, '%')
            or lower(p.category) like concat('%', :query, '%')
          )
        """)
    long countSearch(
            @Param("status") ProductStatus status,
            @Param("query") String query
    );

    @Query("""
        select count(p.id) from Product p
        where p.status = :status
          and lower(p.category) = :category
          and (
               lower(p.name) like concat('%', :query, '%')
            or lower(p.description) like concat('%', :query, '%')
            or lower(p.sku) like concat('%', :query, '%')
          )
        """)
    long countSearchInCategory(
            @Param("status") ProductStatus status,
            @Param("query") String query,
            @Param("category") String category
    );

    @Query("""
        select distinct p.category
        from Product p
        where p.status = com.boutique.productcatalog.entity.ProductStatus.ACTIVE
        order by p.category
        """)
    List<String> findActiveCategories();
}
