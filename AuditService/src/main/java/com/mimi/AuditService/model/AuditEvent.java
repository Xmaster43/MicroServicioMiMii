package com.mimi.AuditService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String topic;

    @Column(name = "kafka_offset")
    private Long kafkaOffset;

    @Column(name = "event_id")
    private String eventId;

    @Column(name = "source_service", nullable = false)
    private String sourceService;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "entity_id")
    private String entityId;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(name = "event_timestamp")
    private LocalDateTime eventTimestamp;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;
}