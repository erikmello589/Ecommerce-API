package com.erikm.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erikm.ecommerce.model.OrderItem;
import java.util.List;
import com.erikm.ecommerce.model.Order;


@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> 
{
    List<OrderItem> findAllByOrder(Order order);
}
