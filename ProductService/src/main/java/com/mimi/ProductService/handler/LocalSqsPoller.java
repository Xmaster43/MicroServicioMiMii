package com.mimi.ProductService.handler;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;

@Component
@Profile("local")
@EnableScheduling
public class LocalSqsPoller {

    private static final Logger log = LoggerFactory.getLogger(LocalSqsPoller.class);

    private final SqsClient sqs;
    private final ReservaEventHandler handler;
    private final String queueUrl;

    public LocalSqsPoller(SqsClient sqs, ReservaEventHandler handler,
                          @Value("${sqs.reserva-product.queue-url}") String queueUrl) {
        this.sqs = sqs;
        this.handler = handler;
        this.queueUrl = queueUrl;
    }

    @Scheduled(fixedDelay = 1000)
    public void poll() {
        List<Message> messages = sqs.receiveMessage(r -> r
                .queueUrl(queueUrl).maxNumberOfMessages(10).waitTimeSeconds(5)).messages();
        if (messages.isEmpty()) return;

        SQSEvent event = new SQSEvent();
        event.setRecords(messages.stream().map(m -> {
            SQSEvent.SQSMessage rec = new SQSEvent.SQSMessage();
            rec.setMessageId(m.messageId());
            rec.setReceiptHandle(m.receiptHandle());
            rec.setBody(m.body());
            return rec;
        }).toList());

        SQSBatchResponse resp = handler.apply(event);
        Set<String> failed = resp.getBatchItemFailures().stream()
                .map(SQSBatchResponse.BatchItemFailure::getItemIdentifier)
                .collect(Collectors.toSet());

        messages.stream()
                .filter(m -> !failed.contains(m.messageId()))
                .forEach(m -> sqs.deleteMessage(d -> d.queueUrl(queueUrl).receiptHandle(m.receiptHandle())));

        if (!failed.isEmpty()) log.warn("{} mensaje(s) volverán a la cola", failed.size());
    }
}