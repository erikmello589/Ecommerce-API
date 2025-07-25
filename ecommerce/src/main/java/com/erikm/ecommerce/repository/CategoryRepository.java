package com.erikm.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.erikm.ecommerce.model.Category;


public interface CategoryRepository extends JpaRepository<Category, Long>
{
    Page<Category> findByIsActiveTrue(Pageable pageable);

    Optional<Category> findByCategoryIdAndIsActiveTrue(Long categoryId);

    Optional<Category> findByNameAndIsActiveTrue(String name);
}
