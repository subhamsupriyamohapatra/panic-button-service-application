package com.emergency.panicbutton.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FastAPI-compatible error body: {@code {"detail": "..."}}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private String detail;
}
