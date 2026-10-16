package com.emergency.panicbutton.device.dto;

import com.emergency.panicbutton.device.enums.PlatformType;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DeviceUpdateRequest {

    private PlatformType platform;

    @Size(min = 5)
    private String fcmToken;

    private Boolean isActive;
}
