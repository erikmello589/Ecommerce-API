package com.erikm.ecommerce.dto;

import java.util.List;

public record OrderDTO(String customerEmail, String shippingAddress, List<OrderItemDTO> orderItens) {

}
