package com.erikm.ecommerce.dto.Responses;

public record LoginResponse(String accessToken, Long expiresIn, String refreshToken) {

}
