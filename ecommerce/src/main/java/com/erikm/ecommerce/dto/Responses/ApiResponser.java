package com.erikm.ecommerce.dto.Responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponser<T> {
    private boolean success;
    private T data;
    private String message;
    private ApiError error;
    private LocalDateTime timestamp;

    public ApiResponser(boolean success, T data, String message, ApiError error) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.error = error;
        this.timestamp = LocalDateTime.now();
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Métodos estáticos de fábrica para facilitar a criação de respostas
    public static <T> ApiResponser<T> success(T data, String message) {
        return new ApiResponser<>(true, data, message, null);
    }

    public static <T> ApiResponser<T> success(T data) {
        return new ApiResponser<>(true, data, "Operação realizada com sucesso", null);
    }

    public static ApiResponser<?> error(String code, String message, String details) {
        return new ApiResponser<>(false, null, null, new ApiError(code, message, details));
    }

    public static ApiResponser<?> error(String code, String message) {
        return new ApiResponser<>(false, null, null, new ApiError(code, message, null));
    }
}