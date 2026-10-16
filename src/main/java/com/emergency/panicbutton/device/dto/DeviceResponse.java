package com.emergency.panicbutton.device.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceResponse {

    private UUID id;

    @JsonProperty("user_id")
    private UUID userId;

    private String deviceId;
    private String platform;
    private String fcmToken;

    @JsonProperty("isActive")
    private boolean isActive;

    @JsonProperty("createdAt")
    private OffsetDateTime createdAt;

    @JsonProperty("updatedAt")
    private OffsetDateTime updatedAt;

    @JsonProperty("lastSeenAt")
    private OffsetDateTime lastSeenAt;
}
