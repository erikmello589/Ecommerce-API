package com.erikm.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erikm.ecommerce.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long>
{
    Optional<Customer> findByDocument(String document);

    Optional<Customer> findByEmail(String email);
}
