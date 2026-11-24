package com.mimi.AuditService.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topic.replication-factor:1}")
    private int replicationFactor;

    @Bean
    public NewTopic authEventsTopic() {
        return TopicBuilder.name("mimii.auth.eventos")
                .partitions(2)
                .replicas(replicationFactor)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE)
                .config(TopicConfig.RETENTION_MS_CONFIG, "604800000") // 7 días
                .build();
    }

    @Bean
    public NewTopic reservasEventsTopic() {
        return TopicBuilder.name("mimii.reservas.eventos")
                .partitions(3)
                .replicas(replicationFactor)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE)
                .config(TopicConfig.RETENTION_MS_CONFIG, "2592000000") // 30 días
                .build();
    }

    @Bean
    public NewTopic authEventsDltTopic() {
        return TopicBuilder.name("mimii.auth.eventos-dlt")
                .partitions(1)
                .replicas(replicationFactor)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE)
                .config(TopicConfig.RETENTION_MS_CONFIG, "1209600000") // 14 días
                .build();
    }

    @Bean
    public NewTopic reservasEventsDltTopic() {
        return TopicBuilder.name("mimii.reservas.eventos-dlt")
                .partitions(1)
                .replicas(replicationFactor)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE)
                .config(TopicConfig.RETENTION_MS_CONFIG, "1209600000") // 14 días
                .build();
    }
}
