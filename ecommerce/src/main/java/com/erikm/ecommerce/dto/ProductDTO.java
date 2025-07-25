package com.erikm.ecommerce.dto;

import java.math.BigDecimal;

public record ProductDTO(String name, String description, BigDecimal price, Integer stockQuantity, String category, String sku, Boolean isActive) {

}
