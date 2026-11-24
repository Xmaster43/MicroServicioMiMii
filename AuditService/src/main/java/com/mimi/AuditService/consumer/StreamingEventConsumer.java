package com.mimi.AuditService.consumer;

import com.mimi.AuditService.dto.StreamEventDTO;
import com.mimi.AuditService.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
@RequiredArgsConstructor
public class StreamingEventConsumer {

    private final AuditService auditService;
    private final Map<String, AtomicLong> contadoresPorServicio = new ConcurrentHashMap<>();

    @KafkaListener(
            topics = {"mimii.auth.eventos", "mimii.reservas.eventos"},
            groupId = "audit-service-group"
    )
    public void consumirEventoAuditoria(
            @Payload StreamEventDTO evento,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.OFFSET) long offset) {

        if (evento == null || evento.getEventType() == null || evento.getSourceService() == null) {
            throw new IllegalArgumentException("Payload inválido o incompleto recibido en topic: " + topic);
        }

        log.info("[audit-service-group] Stream recibido [topic={}, offset={}]: {}", topic, offset, evento);
        auditService.registrarEvento(topic, offset, evento);
    }

    @KafkaListener(
            topics = {"mimii.auth.eventos", "mimii.reservas.eventos"},
            groupId = "analytics-metrics-group"
    )
    public void consumirEventoMetricas(
            @Payload StreamEventDTO evento,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        if (evento == null || evento.getSourceService() == null) {
            return;
        }
        long total = contadoresPorServicio
                .computeIfAbsent(evento.getSourceService(), k -> new AtomicLong(0))
                .incrementAndGet();
        log.info("[analytics-metrics-group] Métrica en tiempo real -> {} lleva {} eventos procesados (topic={})",
                evento.getSourceService(), total, topic);
    }

    public Map<String, Long> obtenerMetricasEnVivo() {
        Map<String, Long> resultado = new ConcurrentHashMap<>();
        contadoresPorServicio.forEach((k, v) -> resultado.put(k, v.get()));
        return resultado;
    }
}