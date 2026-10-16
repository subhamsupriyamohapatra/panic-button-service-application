package com.emergency.panicbutton.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@Tag(name = "Health & Readiness", description = "Liveness and readiness probes")
public class HealthController {

    private final DataSource dataSource;
    private final String serviceName;
    private final String environment;

    public HealthController(
            DataSource dataSource,
            @Value("${spring.application.name:panic-button-service}") String serviceName,
            @Value("${app.environment:production}") String environment) {
        this.dataSource = dataSource;
        this.serviceName = serviceName.toUpperCase();
        this.environment = environment;
    }

    @GetMapping("/health")
    @Operation(summary = "Liveness probe")
    public Map<String, Object> health() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "UP");
        body.put("service", serviceName);
        body.put("timestamp", OffsetDateTime.now());
        return body;
    }

    @GetMapping("/ready")
    @Operation(summary = "Readiness probe (checks database connectivity)")
    public ResponseEntity<Map<String, Object>> ready() {
        boolean connected = databaseConnected();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", connected ? "READY" : "NOT_READY");
        body.put("database", connected ? "CONNECTED" : "DISCONNECTED");
        body.put("environment", environment);
        body.put("timestamp", OffsetDateTime.now());

        return ResponseEntity
                .status(connected ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE)
                .body(body);
    }

    private boolean databaseConnected() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("SELECT 1");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
