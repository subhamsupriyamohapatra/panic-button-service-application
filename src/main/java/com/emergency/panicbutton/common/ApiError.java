package com.emergency.panicbutton.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Error envelope matching the running FastAPI service:
 * {@code {"success": false, "message": "...", "errorCode": "...", "details": ...}}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonPropertyOrder({"success", "message", "errorCode", "details"})
public class ApiError {

    private boolean success;
    private String message;
    private String errorCode;
    private Object details;

    public ApiError(String errorCode, String message, Object details) {
        this(false, message, errorCode, details);
    }

    public static ApiError of(String errorCode, String message) {
        return new ApiError(errorCode, message, null);
    }
}
