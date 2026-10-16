package com.emergency.panicbutton.panic.service;

import com.emergency.panicbutton.panic.dto.MiddlewareCallbackRequest;
import com.emergency.panicbutton.panic.dto.MiddlewareCallbackResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Dispatches panic events to the external middleware service with a bounded
 * retry/backoff policy. Delivery is idempotent via {@code X-Idempotency-Key}.
 */
@Service
public class MiddlewareClient {

    private static final Logger log =
            LoggerFactory.getLogger(MiddlewareClient.class);

    private final RestClient restClient;
    private final String serviceToken;
    private final int maxAttempts;
    private final long backoffMs;

    public MiddlewareClient(
            @Value("${middleware.base-url}") String baseUrl,
            @Value("${middleware.service-token:}") String serviceToken,
            @Value("${middleware.retry.max-attempts:3}") int maxAttempts,
            @Value("${middleware.retry.backoff-ms:500}") long backoffMs) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.serviceToken = serviceToken;
        this.maxAttempts = maxAttempts;
        this.backoffMs = backoffMs;
    }

    public MiddlewareDispatchResult dispatch(
            MiddlewareCallbackRequest payload,
            String correlationId) {

        int attempts = 0;

        while (attempts < maxAttempts) {
            attempts++;
            try {
                MiddlewareCallbackResponse response = restClient.post()
                        .uri("/api/v1/panic/callback")
                        .header("Authorization", "Bearer " + serviceToken)
                        .header("X-Correlation-ID", correlationId)
                        .header("X-Idempotency-Key", payload.getEventId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(payload)
                        .retrieve()
                        .body(MiddlewareCallbackResponse.class);

                return new MiddlewareDispatchResult(true, attempts, response);
            } catch (Exception e) {
                log.warn("Middleware dispatch attempt {}/{} failed: {}",
                        attempts, maxAttempts, e.getMessage());

                if (attempts < maxAttempts) {
                    sleep(backoffMs * attempts);
                }
            }
        }

        return new MiddlewareDispatchResult(false, attempts, null);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
