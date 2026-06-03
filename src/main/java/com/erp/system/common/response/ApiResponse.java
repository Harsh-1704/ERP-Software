package com.erp.system.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private ErrorResponse error;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(String message, String details) {
        return new ApiResponse<>(false, null, message, new ErrorResponse(details), LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message, null, LocalDateTime.now());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorResponse {
        private String details;
        private java.util.Map<String, String> fieldErrors;

        public ErrorResponse(String details) {
            this.details = details;
        }

        public void addFieldError(String field, String message) {
            if (fieldErrors == null) {
                fieldErrors = new java.util.HashMap<>();
            }
            fieldErrors.put(field, message);
        }
    }
}