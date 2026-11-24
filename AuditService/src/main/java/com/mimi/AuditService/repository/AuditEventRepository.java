package com.mimi.AuditService.repository;

import com.mimi.AuditService.model.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {
    List<AuditEvent> findAllByOrderByReceivedAtDesc();
    List<AuditEvent> findByTopicOrderByReceivedAtDesc(String topic);
    List<AuditEvent> findBySourceServiceOrderByReceivedAtDesc(String sourceService);
    List<AuditEvent> findByTopicAndSourceServiceOrderByReceivedAtDesc(String topic, String sourceService);
}