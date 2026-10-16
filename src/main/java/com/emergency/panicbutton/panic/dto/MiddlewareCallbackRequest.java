package com.emergency.panicbutton.panic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Payload sent to the middleware's {@code POST /api/v1/panic/callback}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiddlewareCallbackRequest {

    private String eventId;
    private String userId;
    private String deviceId;
    private String eventType;
    private Double latitude;
    private Double longitude;
    private OffsetDateTime eventTimestamp;
}
