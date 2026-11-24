package com.mimi.AuditService.controller;

import com.mimi.AuditService.model.AuditEvent;
import com.mimi.AuditService.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/events")
    public ResponseEntity<List<AuditEvent>> obtenerEventos(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String sourceService) {
        return ResponseEntity.ok(auditService.listarEventos(topic, sourceService));
    }
}