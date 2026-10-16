package com.emergency.panicbutton.panic.service;

import com.emergency.panicbutton.common.OffsetBasedPageRequest;
import com.emergency.panicbutton.panic.dto.MiddlewareCallbackRequest;
import com.emergency.panicbutton.panic.dto.MiddlewareCallbackResponse;
import com.emergency.panicbutton.panic.dto.PanicEventResponse;
import com.emergency.panicbutton.panic.dto.PanicTriggerRequest;
import com.emergency.panicbutton.panic.entity.PanicEvent;
import com.emergency.panicbutton.panic.repository.PanicEventRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PanicEventService {

    private static final Logger log =
            LoggerFactory.getLogger(PanicEventService.class);

    private final PanicEventRepository panicEventRepository;
    private final MiddlewareClient middlewareClient;

    @Transactional
    public PanicEventResponse trigger(
            UUID userId, PanicTriggerRequest request, String correlationId) {

        String eventId = (request.getEventId() == null || request.getEventId().isBlank())
                ? UUID.randomUUID().toString()
                : request.getEventId();

        Optional<PanicEvent> existing = panicEventRepository.findByEventId(eventId);
        if (existing.isPresent()) {
            return toResponse(existing.get());
        }

        PanicEvent event = new PanicEvent();
        event.setEventId(eventId);
        event.setUserId(userId);
        event.setDeviceId(request.getDeviceId());
        event.setEventType("PANIC");
        event.setLatitude(request.getLatitude());
        event.setLongitude(request.getLongitude());
        event.setEventTimestamp(request.getEventTimestamp());
        event.setStatus("PENDING");
        event.setRetryCount(0);
        panicEventRepository.save(event);

        MiddlewareCallbackRequest payload = new MiddlewareCallbackRequest(
                eventId,
                userId.toString(),
                request.getDeviceId(),
                "PANIC",
                request.getLatitude(),
                request.getLongitude(),
                request.getEventTimestamp());

        MiddlewareDispatchResult result =
                middlewareClient.dispatch(payload, correlationId);

        event.setRetryCount(result.attempts());
        if (result.delivered()) {
            event.setStatus("MIDDLEWARE_SENT");
            event.setMiddlewareResponse(toMap(result.response()));
        } else {
            event.setStatus("MIDDLEWARE_FAILED");
            event.setMiddlewareResponse(Map.of(
                    "error", "Middleware dispatch failed after "
                            + result.attempts() + " attempt(s)"));
        }

        panicEventRepository.save(event);
        log.info("Panic event {} finished with status {}",
                eventId, event.getStatus());

        return toResponse(event);
    }

    @Transactional(readOnly = true)
    public List<PanicEventResponse> list(
            UUID userId, int skip, int limit, boolean allUsers) {

        var pageable = new OffsetBasedPageRequest(skip, limit, Sort.unsorted());
        List<PanicEvent> events = allUsers
                ? panicEventRepository.findAllByOrderByCreatedAtDesc(pageable)
                : panicEventRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        return events.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PanicEventResponse getByEventId(String eventId) {
        return panicEventRepository.findByEventId(eventId)
                .map(this::toResponse)
                .orElseThrow(() -> new NoSuchElementException(
                        "Panic event not found: " + eventId));
    }

    private Map<String, Object> toMap(MiddlewareCallbackResponse response) {
        if (response == null) {
            return null;
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("success", response.isSuccess());
        map.put("eventId", response.getEventId());
        map.put("status", response.getStatus());
        map.put("message", response.getMessage());
        map.put("correlationId", response.getCorrelationId());
        map.put("recipientsNotified", response.getRecipientsNotified());
        return map;
    }

    private PanicEventResponse toResponse(PanicEvent event) {
        return new PanicEventResponse(
                event.getId(),
                event.getEventId(),
                event.getUserId(),
                event.getDeviceId(),
                event.getEventType(),
                event.getLatitude(),
                event.getLongitude(),
                event.getEventTimestamp(),
                event.getStatus(),
                event.getRetryCount(),
                event.getMiddlewareResponse(),
                event.getCreatedAt());
    }
}
