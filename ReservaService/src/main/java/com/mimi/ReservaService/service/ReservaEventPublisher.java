package com.mimi.ReservaService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimi.ReservaService.dto.StreamEventDTO;
import com.mimi.ReservaService.event.CloudEvent;
import com.mimi.ReservaService.event.ReservaCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ReservaEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ReservaEventPublisher.class);
    private static final String RESERVAS_TOPIC = "mimii.reservas.eventos";

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final KafkaEventProducer kafkaEventProducer;

    @Value("${sqs.reserva-product.queue-url}")
    private String queueUrl;

    public ReservaEventPublisher(
            SqsClient sqsClient,
            ObjectMapper objectMapper,
            KafkaEventProducer kafkaEventProducer) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.kafkaEventProducer = kafkaEventProducer;
    }

    public void publishReservaCreada(ReservaCreatedEvent event) {
        CloudEvent<ReservaCreatedEvent> cloudEvent = new CloudEvent<>("com.mimi.reserva", "ReservaCreated", event);
        try {
            String body = objectMapper.writeValueAsString(cloudEvent);
            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(body)
                    .messageGroupId(event.getReservaId().toString())
                    .build();
            sqsClient.sendMessage(request);
            log.info("Published ReservaCreated event to SQS for reservaId={}", event.getReservaId());

            kafkaEventProducer.publicar(
                    RESERVAS_TOPIC,
                    event.getReservaId().toString(),
                    StreamEventDTO.builder()
                            .eventId(UUID.randomUUID().toString())
                            .sourceService("ReservaService")
                            .eventType("RESERVA_CREATED")
                            .entityId(event.getReservaId().toString())
                            .payload(body)
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize ReservaCreatedEvent", e);
            throw new RuntimeException("Error serializando evento", e);
        }
    }
}