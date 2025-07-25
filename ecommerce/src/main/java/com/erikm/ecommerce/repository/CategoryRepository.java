package com.erikm.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erikm.ecommerce.model.Category;


public interface CategoryRepository extends JpaRepository<Category, Long>
{
    Optional<Category> findByName(String name);
}
