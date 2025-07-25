package com.erikm.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erikm.ecommerce.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> 
{
    Page<Order> findByCustomerCustomerId(Long customerId, Pageable pageable);
}
