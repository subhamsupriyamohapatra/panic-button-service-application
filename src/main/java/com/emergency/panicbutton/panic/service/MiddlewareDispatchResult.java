package com.emergency.panicbutton.panic.service;

import com.emergency.panicbutton.panic.dto.MiddlewareCallbackResponse;

public record MiddlewareDispatchResult(
        boolean delivered,
        int attempts,
        MiddlewareCallbackResponse response) {
}
