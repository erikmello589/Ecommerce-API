package com.erikm.ecommerce.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.erikm.ecommerce.dto.ApiResponse;
import com.erikm.ecommerce.dto.OrderDTO;
import com.erikm.ecommerce.model.Order;
import com.erikm.ecommerce.service.OrderService;

import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Pedido", description = "Endpoints para gerenciamento de Pedidos e suas informações.")
public class OrderController 
{
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/api/orders")
    public ResponseEntity<ApiResponse<?>> newOrder(@RequestBody OrderDTO orderDTO) 
    {
        try 
        {
            Order call = orderService.createNewOrder(orderDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(call, "Pedido criado com sucesso."));
        } 
        catch (ResponseStatusException e) 
        {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }
}
