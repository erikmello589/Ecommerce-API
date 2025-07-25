package com.erikm.ecommerce.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        
        Customer customer = customerService.findCustomerByEmail(orderDTO.customerEmail());

        
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

    public Page<Order> listAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Transactional
    public OrderStatus editStatusOrder(Long orderId, String statusRequest) 
    {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado."));

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pedido não pode ser modificado, graças ao status: " + order.getStatus());
        }

        OrderStatus newStatus;
        try 
        {
            newStatus = OrderStatus.valueOf(statusRequest.toUpperCase());
        } 
        catch (IllegalArgumentException e) 
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O status fornecido é inválido. Status permitidos: " + java.util.Arrays.toString(OrderStatus.values()));
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
        return newStatus;
    }

    
    @Transactional
    public Order deleteOrder(Long orderId)
    {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado."));

        List<OrderItem> orderItems = orderItemRepository.findAllByOrder(order);
        if (order.getStatus() == OrderStatus.CONFIRMED || order.getStatus() == OrderStatus.CREATED) 
        {
            for (OrderItem item : orderItems) {
                Product product = item.getProduct();
                int returnedQuantity = item.getQuantity();
                
                //Devolver pro estoque
                productService.editStock(product.getProductId(), product.getStockQuantity() + returnedQuantity);
            }
        } 
        else if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) 
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pedido não pode ser cancelado, graças ao status: " + order.getStatus());
        } 

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    public Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado."));
    }

    public Page<Order> findOrdersByCustomerId(Long customerId, Pageable pageable) 
    {
        customerService.findCustomerById(customerId);
        return orderRepository.findByCustomerCustomerId(customerId, pageable);
    }

    public OrderDTO convertToDto(Order order) {
        return modelMapper.map(order, OrderDTO.class);
    }

    public Order convertToEntity(OrderDTO orderDTO) {
        return modelMapper.map(orderDTO, Order.class);
    }
}