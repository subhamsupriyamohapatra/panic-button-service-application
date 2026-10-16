package com.emergency.panicbutton.device.controller;

import com.emergency.panicbutton.common.ApiResponse;
import com.emergency.panicbutton.config.JwtAuthFilter;
import com.emergency.panicbutton.device.dto.DeviceRegisterRequest;
import com.emergency.panicbutton.device.dto.DeviceResponse;
import com.emergency.panicbutton.device.dto.DeviceUpdateRequest;
import com.emergency.panicbutton.device.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@SecurityRequirement(name = "HTTPBearer")
@Tag(name = "Devices", description = "Register and manage user devices")
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping("/devices/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register or update a device and FCM token")
    public ApiResponse<DeviceResponse> register(
            HttpServletRequest request,
            @Valid @RequestBody DeviceRegisterRequest body) {
        return ApiResponse.ok(deviceService.register(currentUser(request), body));
    }

    @PutMapping("/devices/{device_id}")
    @Operation(summary = "Update device status or FCM token")
    public ApiResponse<DeviceResponse> update(
            HttpServletRequest request,
            @PathVariable("device_id") String deviceId,
            @Valid @RequestBody DeviceUpdateRequest body) {
        return ApiResponse.ok(
                deviceService.update(currentUser(request), deviceId, body));
    }

    @DeleteMapping("/devices/{device_id}")
    @Operation(summary = "Unregister/delete a device")
    public ApiResponse<Object> delete(
            HttpServletRequest request,
            @PathVariable("device_id") String deviceId) {
        deviceService.delete(currentUser(request), deviceId);
        return ApiResponse.ok(null, "Device deleted successfully");
    }

    @GetMapping("/users/{user_id}/devices")
    @Operation(summary = "Get all registered devices for a user")
    public ApiResponse<List<DeviceResponse>> listByUser(
            @PathVariable("user_id") UUID userId) {
        return ApiResponse.ok(deviceService.listByUser(userId));
    }

    private UUID currentUser(HttpServletRequest request) {
        return (UUID) request.getAttribute(JwtAuthFilter.AUTH_USER_ATTRIBUTE);
    }
}
