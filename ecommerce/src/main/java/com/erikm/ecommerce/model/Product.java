package com.erikm.ecommerce.model;

import java.math.BigDecimal;

import com.erikm.ecommerce.model.Utils.Timestamps;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tb_products") 
public class Product extends Timestamps
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @NotBlank(message = "O nome do produto é obrigatório e não pode estar em branco.")
    @Size(min = 3, max = 100, message = "O nome do produto deve ter entre 3 e 100 caracteres.")
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Size(max = 500, message = "A descrição do produto não pode exceder 500 caracteres.")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "O preço do produto é obrigatório.")
    @DecimalMin(value = "0.01", message = "O preço do produto deve ser maior que zero.")
    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @NotNull(message = "A quantidade em estoque é obrigatória.")
    @Min(value = 0, message = "A quantidade em estoque não pode ser negativa.")
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    @NotNull(message = "A categoria do produto é obrigatória.")
    @Valid
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @NotBlank(message = "O SKU do produto é obrigatório e não pode estar em branco.")
    @Size(min = 3, max = 50, message = "O SKU deve ter entre 3 e 50 caracteres.")
    @Column(name = "sku", length = 50, unique = true, nullable = false)
    private String sku;

    @NotNull(message = "O status de atividade do produto é obrigatório.")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public Product() 
    {
        super();
    }

     // Construtor para testes, incluindo a categoria
    public Product(String name, String description, BigDecimal price, Integer stockQuantity, Category category, String sku, Boolean isActive) {
        super();
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.sku = sku;
        this.isActive = isActive;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
