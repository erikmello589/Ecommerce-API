package com.erikm.ecommerce.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.erikm.ecommerce.dto.OrderDTO;
import com.erikm.ecommerce.dto.Responses.ApiResponse;
import com.erikm.ecommerce.dto.Responses.PageResponse;
import com.erikm.ecommerce.model.Order;
import com.erikm.ecommerce.model.Enums.OrderStatus;
import com.erikm.ecommerce.service.OrderService;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/api/orders")
    public ResponseEntity<PageResponse<Order>> getAllOrders(@ParameterObject Pageable pageable) 
    {
        Page<Order> call = orderService.listAllOrders(pageable);
        PageResponse<Order> pageResponse = PageResponse.fromSpringPage(call);
        return ResponseEntity.status(HttpStatus.OK).body(pageResponse);
    }

    @GetMapping("/api/orders/{id}")
    public ResponseEntity<ApiResponse<?>> getOrderbyId(@PathVariable("id") Long orderId)
    {
        try 
        {
            Order call = orderService.findOrderById(orderId);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(call, "Pedido Listado com sucesso."));
        } 
        catch (ResponseStatusException e) 
        {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }

    @DeleteMapping("/api/orders/{id}")
    public ResponseEntity<ApiResponse<?>> deleteOrderbyId(@PathVariable("id") Long orderId)
    {
        try 
        {
            Order call = orderService.deleteOrder(orderId);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(call, "Pedido cancelado com sucesso."));
        } 
        catch (ResponseStatusException e) 
        {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }

    @GetMapping("/api/orders/customer/{customerId}")
    public ResponseEntity<PageResponse<Order>> getOrdersbyCustomerId(@PathVariable("customerId") Long customerId, @ParameterObject Pageable pageable)
    {
        Page<Order> call = orderService.findOrdersByCustomerId(customerId, pageable);
        PageResponse<Order> pageResponse = PageResponse.fromSpringPage(call);
        return ResponseEntity.status(HttpStatus.OK).body(pageResponse);
    }

    @PatchMapping("/api/orders/{id}/status")
    public ResponseEntity<ApiResponse<?>> editStock(@PathVariable("id") Long orderId, @RequestParam String orderStatus) 
    {
        try 
        {
            OrderStatus call = orderService.editStatusOrder(orderId, orderStatus);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(call, "Status do Pedido alterado com sucesso."));
        } 
        catch (ResponseStatusException e) 
        {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getStatusCode().toString(), e.getTypeMessageCode(), e.getReason()));
        }
    }
}
