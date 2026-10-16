package com.emergency.panicbutton.panic.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "panic_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanicEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "event_id", nullable = false, unique = true)
    private String eventId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "event_type")
    private String eventType;

    private Double latitude;

    private Double longitude;

    @Column(name = "event_timestamp")
    private OffsetDateTime eventTimestamp;

    @Column(name = "status", nullable = false)
    private String status = "PENDING";

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "middleware_response", columnDefinition = "jsonb")
    private Map<String, Object> middlewareResponse;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
        if (status == null) {
            status = "PENDING";
        }
    }
}
