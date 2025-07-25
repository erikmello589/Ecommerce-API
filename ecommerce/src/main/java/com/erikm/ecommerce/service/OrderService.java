package com.erikm.ecommerce.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.erikm.ecommerce.dto.OrderDTO;
import com.erikm.ecommerce.dto.OrderItemDTO;
import com.erikm.ecommerce.model.Customer;
import com.erikm.ecommerce.model.Order;
import com.erikm.ecommerce.model.OrderItem;
import com.erikm.ecommerce.model.Product;
import com.erikm.ecommerce.model.Enums.OrderStatus;
import com.erikm.ecommerce.repository.OrderItemRepository;
import com.erikm.ecommerce.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerService customerService;
    private final ProductService productService;
    private final ModelMapper modelMapper;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                        CustomerService customerService, ProductService productService,
                        ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.customerService = customerService;
        this.productService = productService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public Order createNewOrder(OrderDTO orderDTO) 
    {
        // 1. Busca o cliente. Se não encontrar, findCustomerByEmail já lança NOT_FOUND.
        Customer customer = customerService.findCustomerByEmail(orderDTO.customerEmail());

        // 2. Valida se há itens no pedido.
        if (orderDTO.orderItens() == null || orderDTO.orderItens().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O pedido deve conter pelo menos um item.");
        }

        Order newOrder = new Order();
        newOrder.setCustomer(customer);
        newOrder.setShippingAddress(orderDTO.shippingAddress());
        newOrder.setStatus(OrderStatus.CREATED);
        newOrder.setTotalAmount(BigDecimal.ZERO);
        newOrder = orderRepository.save(newOrder);

        List<OrderItem> orderItemsToSave = new ArrayList<>();
        BigDecimal calculatedTotalAmount = BigDecimal.ZERO;
        boolean hasInsufficientStock = false; 

        // 4. Itera sobre os itens do DTO para criar os OrderItems e verificar o estoque.
        // Esta é a ÚNICA iteração necessária.
        for (OrderItemDTO itemDTO : orderDTO.orderItens())
        {
            Product product = productService.findProductBySku(itemDTO.sku());

            if (product.getStockQuantity() < itemDTO.quantity()) 
            {
                hasInsufficientStock = true; 
            }
            
            OrderItem newOrderItem = new OrderItem();
            newOrderItem.setOrder(newOrder); 
            newOrderItem.setProduct(product);
            newOrderItem.setQuantity(itemDTO.quantity());
            newOrderItem.setUnitPrice(product.getPrice()); 
            newOrderItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.quantity())));

            orderItemsToSave.add(newOrderItem);
            calculatedTotalAmount = calculatedTotalAmount.add(newOrderItem.getSubtotal());
        }

        
        orderItemRepository.saveAll(orderItemsToSave);
        newOrder.setTotalAmount(calculatedTotalAmount); 

        if (hasInsufficientStock) 
        {
            newOrder.setStatus(OrderStatus.PENDING); // Pelo menos um item não tinha estoque
        } 
        else 
        {
            newOrder.setStatus(OrderStatus.CONFIRMED); 
            for (OrderItem orderItem : orderItemsToSave) 
            {
                productService.editStock(orderItem.getProduct().getProductId(), orderItem.getProduct().getStockQuantity() - orderItem.getQuantity());
            }
        }

        return orderRepository.save(newOrder);
    }

    public List<Order> listAllOrders() {
        return orderRepository.findAll();
        // TODO Regra de negócios para Auth: Usuário Admin receberá todas as informações (incluindo timestamps) e usuário deslogado apenas receberá DTOs
    }

    public Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado."));
    }

    public OrderDTO convertToDto(Order order) {
        return modelMapper.map(order, OrderDTO.class);
    }

    public Order convertToEntity(OrderDTO orderDTO) {
        return modelMapper.map(orderDTO, Order.class);
    }
}