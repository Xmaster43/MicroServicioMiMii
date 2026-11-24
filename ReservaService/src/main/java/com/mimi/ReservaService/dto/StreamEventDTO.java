package com.mimi.ReservaService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreamEventDTO {
    private String eventId;
    private String sourceService;
    private String eventType;
    private String entityId;
    private String payload;
    private LocalDateTime timestamp;
}
