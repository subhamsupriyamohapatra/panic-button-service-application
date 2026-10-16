package com.emergency.panicbutton.device.dto;

import com.emergency.panicbutton.device.enums.PlatformType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DeviceRegisterRequest {

    @NotBlank
    @Size(min = 2, max = 100)
    private String deviceId;

    @NotNull
    private PlatformType platform;

    @NotBlank
    @Size(min = 5)
    private String fcmToken;
}
