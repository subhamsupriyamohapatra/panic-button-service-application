package com.emergency.panicbutton.panic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiddlewareCallbackResponse {

    private boolean success;
    private String eventId;
    private String status;
    private String message;
    private String correlationId;
    private int recipientsNotified;
}
