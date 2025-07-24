package com.erikm.ecommerce.dto;

import java.time.LocalDateTime;

public record RequestResponseDTO<T>(Boolean success, T data, String message, LocalDateTime timestamp) 
{

}
