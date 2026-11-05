package com.mimi.ProductService.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
@Profile("local")
public class LocalSqsConfig {

    @Bean
    public SqsClient sqsClient(@Value("${aws.region}") String region,
                               @Value("${aws.sqs.endpoint}") String endpoint) {
        return SqsClient.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}