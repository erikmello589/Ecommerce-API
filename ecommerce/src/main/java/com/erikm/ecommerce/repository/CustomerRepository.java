package com.erikm.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.erikm.ecommerce.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long>
{

    Page<Customer> findByIsActiveTrue(Pageable pageable);

    Optional<Customer> findByCustomerIdAndIsActiveTrue(Long customerId);

    Optional<Customer> findByDocumentAndIsActiveTrue(String document);

    Optional<Customer> findByEmailAndIsActiveTrue(String email);
}
