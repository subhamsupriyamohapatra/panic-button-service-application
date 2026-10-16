package com.emergency.panicbutton.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Envelope used by every panic-button-service response, matching FastAPI's
 * {@code APIResponse[T]} schema.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonPropertyOrder({"success", "message", "data", "errorCode"})
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String errorCode;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "Operation completed successfully", data, null);
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static <T> ApiResponse<T> error(String errorCode, String message) {
        return new ApiResponse<>(false, message, null, errorCode);
    }
}
