package com.erikm.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private ApiError error;
    private Instant timestamp;

    public ApiResponse(boolean success, T data, String message, ApiError error) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.error = error;
        this.timestamp = Instant.now();
    }

    // Getters para todos os campos
    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public ApiError getError() {
        return error;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    // Métodos estáticos de fábrica para facilitar a criação de respostas
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, "Operação realizada com sucesso", null);
    }

    public static ApiResponse<?> error(String code, String message, String details) {
        return new ApiResponse<>(false, null, null, new ApiError(code, message, details));
    }

    public static ApiResponse<?> error(String code, String message) {
        return new ApiResponse<>(false, null, null, new ApiError(code, message, null));
    }
}