package com.emergency.panicbutton.panic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class PanicTriggerRequest {

    @NotBlank
    @Size(min = 2, max = 100)
    private String deviceId;

    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double latitude;

    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double longitude;

    @NotNull
    @JsonProperty("eventTimestamp")
    private OffsetDateTime eventTimestamp;

    @JsonProperty("eventId")
    private String eventId;
}
