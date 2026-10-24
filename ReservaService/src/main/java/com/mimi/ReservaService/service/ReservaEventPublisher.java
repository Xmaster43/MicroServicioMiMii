package com.mimi.ReservaService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimi.ReservaService.event.CloudEvent;
import com.mimi.ReservaService.event.ReservaCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class ReservaEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ReservaEventPublisher.class);

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${sqs.reserva-product.queue-url}")
    private String queueUrl;

    public ReservaEventPublisher(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
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
            log.info("Published ReservaCreated event for reservaId={}", event.getReservaId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize ReservaCreatedEvent", e);
            throw new RuntimeException("Error serializando evento", e);
        }
    }
}