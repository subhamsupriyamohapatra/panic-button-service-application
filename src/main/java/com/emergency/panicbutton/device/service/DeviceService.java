package com.emergency.panicbutton.device.service;

import com.emergency.panicbutton.device.dto.DeviceRegisterRequest;
import com.emergency.panicbutton.device.dto.DeviceResponse;
import com.emergency.panicbutton.device.dto.DeviceUpdateRequest;
import com.emergency.panicbutton.device.entity.Device;
import com.emergency.panicbutton.device.repository.DeviceRepository;
import com.emergency.panicbutton.user.entity.User;
import com.emergency.panicbutton.user.repository.UserRepository;
import com.emergency.panicbutton.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public DeviceResponse register(UUID userId, DeviceRegisterRequest request) {
        userService.getUser(userId);

        Optional<Device> existing = deviceRepository
                .findByUserIdAndDeviceId(userId, request.getDeviceId())
                .or(() -> deviceRepository.findByFcmToken(request.getFcmToken()));

        Device device = existing.orElseGet(Device::new);
        device.setUser(userRepository.getReferenceById(userId));
        device.setDeviceId(request.getDeviceId());
        device.setPlatform(request.getPlatform());
        device.setFcmToken(request.getFcmToken());
        device.setActive(true);
        device.setLastSeenAt(OffsetDateTime.now());

        return toResponse(deviceRepository.save(device));
    }

    @Transactional
    public DeviceResponse update(UUID userId, String deviceId, DeviceUpdateRequest request) {
        Device device = resolve(userId, deviceId);

        if (request.getPlatform() != null) {
            device.setPlatform(request.getPlatform());
        }
        if (request.getFcmToken() != null && !request.getFcmToken().isBlank()) {
            device.setFcmToken(request.getFcmToken());
        }
        if (request.getIsActive() != null) {
            device.setActive(request.getIsActive());
        }
        device.setLastSeenAt(OffsetDateTime.now());

        return toResponse(deviceRepository.save(device));
    }

    @Transactional
    public void delete(UUID userId, String deviceId) {
        Device device = resolve(userId, deviceId);
        deviceRepository.delete(device);
    }

    @Transactional(readOnly = true)
    public List<DeviceResponse> listByUser(UUID userId) {
        userService.getUser(userId);
        return deviceRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    private Device resolve(UUID userId, String deviceId) {
        return deviceRepository.findByUserIdAndDeviceId(userId, deviceId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Device not found: " + deviceId));
    }

    private DeviceResponse toResponse(Device device) {
        User user = device.getUser();
        return new DeviceResponse(
                device.getId(),
                user != null ? user.getId() : null,
                device.getDeviceId(),
                device.getPlatform() != null ? device.getPlatform().name() : null,
                device.getFcmToken(),
                device.isActive(),
                device.getCreatedAt(),
                device.getUpdatedAt(),
                device.getLastSeenAt());
    }
}
