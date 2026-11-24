package com.mimi.AuditService.service;

import com.mimi.AuditService.dto.StreamEventDTO;
import com.mimi.AuditService.model.AuditEvent;
import com.mimi.AuditService.repository.AuditEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditEventRepository auditEventRepository;

    public AuditEvent registrarEvento(String topic, long offset, StreamEventDTO dto) {
        AuditEvent event = AuditEvent.builder()
                .topic(topic)
                .kafkaOffset(offset)
                .eventId(dto.getEventId())
                .sourceService(dto.getSourceService() != null ? dto.getSourceService() : "UNKNOWN")
                .eventType(dto.getEventType() != null ? dto.getEventType() : "EVENT")
                .entityId(dto.getEntityId())
                .payload(dto.getPayload())
                .eventTimestamp(dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now())
                .receivedAt(LocalDateTime.now())
                .build();

        return auditEventRepository.save(event);
    }

    public List<AuditEvent> listarEventos(String topic, String sourceService) {
        boolean hasTopic = topic != null && !topic.isBlank();
        boolean hasService = sourceService != null && !sourceService.isBlank();

        if (hasTopic && hasService) {
            return auditEventRepository.findByTopicAndSourceServiceOrderByReceivedAtDesc(topic, sourceService);
        }
        if (hasTopic) {
            return auditEventRepository.findByTopicOrderByReceivedAtDesc(topic);
        }
        if (hasService) {
            return auditEventRepository.findBySourceServiceOrderByReceivedAtDesc(sourceService);
        }
        return auditEventRepository.findAllByOrderByReceivedAtDesc();
    }
}
