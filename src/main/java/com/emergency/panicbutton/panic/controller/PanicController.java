package com.emergency.panicbutton.panic.controller;

import com.emergency.panicbutton.common.ApiResponse;
import com.emergency.panicbutton.config.CorrelationIdFilter;
import com.emergency.panicbutton.config.JwtAuthFilter;
import com.emergency.panicbutton.panic.dto.PanicEventResponse;
import com.emergency.panicbutton.panic.dto.PanicTriggerRequest;
import com.emergency.panicbutton.panic.service.PanicEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/panic")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "HTTPBearer")
@Tag(name = "Panic Button", description = "Trigger and query panic events")
public class PanicController {

    private final PanicEventService panicEventService;

    @PostMapping("/trigger")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Trigger a panic button emergency event")
    public ApiResponse<PanicEventResponse> trigger(
            HttpServletRequest request,
            @RequestHeader(value = CorrelationIdFilter.HEADER, required = false)
            String correlationId,
            @Valid @RequestBody PanicTriggerRequest body) {

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
        }

        UUID userId = (UUID) request.getAttribute(JwtAuthFilter.AUTH_USER_ATTRIBUTE);
        return ApiResponse.ok(panicEventService.trigger(userId, body, correlationId));
    }

    @GetMapping("/events")
    @Operation(summary = "List panic events for current user or all events")
    public ApiResponse<List<PanicEventResponse>> list(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") @Min(0) int skip,
            @RequestParam(defaultValue = "50") @Min(1) @Max(100) int limit,
            @RequestParam(name = "all_users", defaultValue = "false") boolean allUsers) {

        UUID userId = (UUID) request.getAttribute(JwtAuthFilter.AUTH_USER_ATTRIBUTE);
        return ApiResponse.ok(panicEventService.list(userId, skip, limit, allUsers));
    }

    @GetMapping("/events/{event_id}")
    @Operation(summary = "Get single panic event details by eventId")
    public ApiResponse<PanicEventResponse> getByEventId(
            @PathVariable("event_id") String eventId) {
        return ApiResponse.ok(panicEventService.getByEventId(eventId));
    }
}
