package com.erikm.ecommerce.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "tb_order_items")
public class OrderItem 
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    @NotNull(message = "O pedido pertencente é obrigatório")
    @Valid
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false) 
    private Order order;

    @NotNull(message = "O produto referenciado é obrigatório")
    @Valid
    @ManyToOne 
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull(message = "A quantidade de itens é obrigatória.")
    @Min(value = 0, message = "A quantidade de itens não pode ser negativa.")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull(message = "O valor unitário do item é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor unitário do item deve ser maior que zero.")
    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @NotNull(message = "O subtotal do itens é obrigatório.")
    @DecimalMin(value = "0.01", message = "O subtotal do itens deve ser maior que zero.")
    @Column(name = "subtotal", precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal;

    public OrderItem() 
    {
        
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    
}
