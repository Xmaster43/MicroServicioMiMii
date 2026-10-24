package com.mimi.ProductService.handler;

import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimi.ProductService.event.CloudEvent;
import com.mimi.ProductService.event.ReservaCreatedEvent;
import com.mimi.ProductService.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
public class ReservaEventHandler implements Function<SQSEvent, SQSBatchResponse> {

    private static final Logger log = LoggerFactory.getLogger(ReservaEventHandler.class);

    private final ProductService productService;
    private final ObjectMapper objectMapper;

    public ReservaEventHandler(ProductService productService, ObjectMapper objectMapper) {
        this.productService = productService;
        this.objectMapper = objectMapper;
    }

    @Override
    public SQSBatchResponse apply(SQSEvent sqsEvent) {
        List<SQSBatchResponse.BatchItemFailure> failures = new ArrayList<>();

        // Iteramos sobre el lote completo que nos envía SQS (hasta 10 mensajes)
        for (SQSEvent.SQSMessage message : sqsEvent.getRecords()) {
            try {
                String payload = message.getBody();
                CloudEvent<ReservaCreatedEvent> cloudEvent = objectMapper.readValue(payload,
                        objectMapper.getTypeFactory().constructParametricType(CloudEvent.class, ReservaCreatedEvent.class));
                
                ReservaCreatedEvent event = cloudEvent.getData();
                log.info("Received ReservaCreated event for reservaId={}", event.getReservaId());
                
                productService.handleReservaCreated(event);
                
            } catch (Exception e) {
                log.error("Error processing message ID {}: {}", message.getMessageId(), e.getMessage());
                // Si este mensaje falla, lo agregamos a la lista negra.
                // AWS Lambda borrará los exitosos y devolverá solo este a la cola SQS.
                failures.add(new SQSBatchResponse.BatchItemFailure(message.getMessageId()));
            }
        }

        // Retornamos el reporte oficial a AWS
        return new SQSBatchResponse(failures);
    }
}