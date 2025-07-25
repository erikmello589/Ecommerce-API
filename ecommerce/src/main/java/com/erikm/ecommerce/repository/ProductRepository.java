package com.erikm.ecommerce.repository;

import com.erikm.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByIsActiveTrue(Pageable pageable);

    Optional<Product> findByProductIdAndIsActiveTrue(Long productId);

    Optional<Product> findBySkuAndIsActiveTrue(String sku);

    Page<Product> findByCategoryNameContainingIgnoreCaseAndIsActiveTrue(String categoryName, Pageable pageable);

    
    Page<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(String name, Pageable pageable);

    Page<Product> findByCategoryCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);

    Page<Product> findByPriceBetweenAndIsActiveTrue(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndCategoryCategoryIdAndIsActiveTrue(String name, Long categoryId, Pageable pageable);
}