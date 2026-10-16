package com.emergency.panicbutton.panic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanicEventResponse {

    private UUID id;

    @JsonProperty("eventId")
    private String eventId;

    @JsonProperty("userId")
    private UUID userId;

    @JsonProperty("deviceId")
    private String deviceId;

    @JsonProperty("eventType")
    private String eventType;

    private Double latitude;
    private Double longitude;

    @JsonProperty("eventTimestamp")
    private OffsetDateTime eventTimestamp;

    private String status;

    @JsonProperty("retryCount")
    private int retryCount;

    @JsonProperty("middlewareResponse")
    private Map<String, Object> middlewareResponse;

    @JsonProperty("createdAt")
    private OffsetDateTime createdAt;
}
