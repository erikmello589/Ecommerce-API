package com.erikm.ecommerce.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.erikm.ecommerce.model.Enums.OrderStatus;
import com.erikm.ecommerce.model.Utils.Timestamps;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tb_orders")
public class Order extends Timestamps 
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @NotNull(message = "O cliente do pedido é obrigatório.")
    @Valid
    @ManyToOne 
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull(message = "O status do pedido é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private OrderStatus status;

    @NotNull(message = "O valor total do pedido é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor total do pedido deve ser maior que zero.")
    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @NotBlank(message = "O endereço de entrega é obrigatório e não pode estar em branco.")
    @Size(max = 500, message = "O endereço de entrega não pode exceder 500 caracteres.")
    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;

    @CreationTimestamp
    @Column(name = "order_date", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Order() 
    {
        super();
        this.status = OrderStatus.PENDING;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
    
}
