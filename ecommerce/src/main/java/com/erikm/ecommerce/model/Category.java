package com.erikm.ecommerce.model;

import com.erikm.ecommerce.model.Utils.Timestamps;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table (name = "tb_categories")
public class Category extends Timestamps
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @NotBlank(message = "O nome da categoria é obrigatório e não pode estar em branco.")
    @Size(min = 3, max = 30, message = "O nome da categoria deve ter entre 3 e 30 caracteres.")
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Size(max = 500, message = "A descrição da categoria não pode exceder 500 caracteres.")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "O status de atividade da categoria não pode ser nulo.")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Category() 
    {
        super(); 
    }
    
}
