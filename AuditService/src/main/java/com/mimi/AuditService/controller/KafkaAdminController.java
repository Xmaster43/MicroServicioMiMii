package com.mimi.AuditService.controller;

import com.mimi.AuditService.consumer.StreamingEventConsumer;
import com.mimi.AuditService.dto.ConsumerGroupInfoDTO;
import com.mimi.AuditService.dto.TopicInfoDTO;
import com.mimi.AuditService.dto.TopicRequestDTO;
import com.mimi.AuditService.service.KafkaAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/kafka-admin")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class KafkaAdminController {

    private final KafkaAdminService kafkaAdminService;
    private final StreamingEventConsumer streamingEventConsumer;

    @GetMapping("/topics")
    public ResponseEntity<List<TopicInfoDTO>> listarTopicos() {
        return ResponseEntity.ok(kafkaAdminService.listarTopicos());
    }

    @PostMapping("/topics")
    public ResponseEntity<Map<String, String>> crearTopico(@Valid @RequestBody TopicRequestDTO request) {
        kafkaAdminService.crearTopico(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Tópico creado: " + request.getName()));
    }

    @PutMapping("/topics/{name}/config")
    public ResponseEntity<Map<String, String>> actualizarConfiguracion(
            @PathVariable String name,
            @RequestParam(defaultValue = "604800000") String retentionMs,
            @RequestParam(defaultValue = "delete") String cleanupPolicy) {
        kafkaAdminService.actualizarConfiguracion(name, retentionMs, cleanupPolicy);
        return ResponseEntity.ok(Map.of("message", "Configuración actualizada: " + name));
    }

    @DeleteMapping("/topics/{name}")
    public ResponseEntity<Map<String, String>> eliminarTopico(@PathVariable String name) {
        kafkaAdminService.eliminarTopico(name);
        return ResponseEntity.ok(Map.of("message", "Tópico eliminado: " + name));
    }

    @GetMapping("/consumer-groups")
    public ResponseEntity<List<ConsumerGroupInfoDTO>> listarConsumerGroups() {
        return ResponseEntity.ok(kafkaAdminService.listarConsumerGroups());
    }

    @GetMapping("/metrics")
    public ResponseEntity<?> obtenerMetricasStreaming() {
        return ResponseEntity.ok(streamingEventConsumer.obtenerMetricasEnVivo());
    }
}