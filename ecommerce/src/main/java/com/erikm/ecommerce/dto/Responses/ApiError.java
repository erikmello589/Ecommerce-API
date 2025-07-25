package com.erikm.ecommerce.dto.Responses;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private String code;
    private String message;
    private String details;

    public ApiError(String code, String message, String details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }

    // Getters
    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }
}