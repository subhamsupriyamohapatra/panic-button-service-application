package com.emergency.panicbutton.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FastAPI-compatible 422 validation error body: {@code {"detail": [...]}}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpValidationError {

    private List<ValidationErrorItem> detail;
}
