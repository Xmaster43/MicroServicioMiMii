package com.mimi.AuditService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicRequestDTO {
    private String name;
    private int partitions = 2;
    private short replicationFactor = 1;
    private String retentionMs = "604800000";
    private String cleanupPolicy = "delete";
}

