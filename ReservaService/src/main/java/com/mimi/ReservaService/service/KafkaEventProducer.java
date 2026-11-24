package com.mimi.ReservaService.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.mimi.ReservaService.dto.StreamEventDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventProducer {

    private final KafkaTemplate<String, StreamEventDTO> kafkaTemplate;

    public void publicar(String topic, String key, StreamEventDTO event) {
        try {
            kafkaTemplate.send(topic, key, event).whenComplete((result, ex) -> {
                if (ex != null) {
                    log.warn("Kafka no disponible para topic {}: {}", topic, ex.getMessage());
                } else {
                    log.info("Evento enviado a Kafka [topic={}, key={}]", topic, key);
                }
            });
        } catch (Exception e) {
            log.warn("Error sincrono enviando a Kafka [topic={}]: {}", topic, e.getMessage());
        }
    }
}
