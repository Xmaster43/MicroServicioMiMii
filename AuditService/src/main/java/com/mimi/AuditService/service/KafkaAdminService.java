package com.mimi.AuditService.service;

import com.mimi.AuditService.dto.TopicRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import com.mimi.AuditService.dto.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaAdminService {

    private final KafkaAdmin kafkaAdmin;

    @FunctionalInterface
    private interface AdminOperation<T> {
        T run(AdminClient admin) throws Exception;
    }

    private <T> T conAdmin(String accion, AdminOperation<T> op) {
        try (AdminClient admin = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            return op.run(admin);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Operación interrumpida al " + accion, e);
        } catch (Exception e) {
            Throwable causa = (e instanceof ExecutionException && e.getCause() != null) ? e.getCause() : e;
            log.error("Error al {}: {}", accion, causa.getMessage(), causa);
            throw new IllegalStateException("Error al " + accion + ": " + causa.getMessage(), causa);
        }
    }

    public List<TopicInfoDTO> listarTopicos() {
        return conAdmin("listar tópicos", admin -> {
            Set<String> nombres = admin.listTopics().names().get();
            if (nombres.isEmpty()) return List.<TopicInfoDTO>of();
            return admin.describeTopics(nombres).allTopicNames().get().values().stream()
                    .map(d -> new TopicInfoDTO(
                            d.name(),
                            d.partitions().size(),
                            d.partitions().isEmpty() ? 0 : d.partitions().get(0).replicas().size()))
                    .toList();
        });
    }

    public void crearTopico(TopicRequestDTO req) {
        conAdmin("crear tópico " + req.getName(), admin -> {
            NewTopic nuevo = new NewTopic(req.getName(), req.getPartitions(), req.getReplicationFactor())
                    .configs(Map.of(
                            "retention.ms", req.getRetentionMs(),
                            "cleanup.policy", req.getCleanupPolicy()));
            admin.createTopics(List.of(nuevo)).all().get();
            return null;
        });
    }

    public void actualizarConfiguracion(String topic, String retentionMs, String cleanupPolicy) {
        conAdmin("actualizar configuración de " + topic, admin -> {
            ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, topic);
            Collection<AlterConfigOp> ops = List.of(
                    new AlterConfigOp(new ConfigEntry("retention.ms", retentionMs), AlterConfigOp.OpType.SET),
                    new AlterConfigOp(new ConfigEntry("cleanup.policy", cleanupPolicy), AlterConfigOp.OpType.SET));
            admin.incrementalAlterConfigs(Map.of(resource, ops)).all().get();
            return null;
        });
    }

    public void eliminarTopico(String topic) {
        conAdmin("eliminar tópico " + topic, admin -> {
            admin.deleteTopics(List.of(topic)).all().get();
            return null;
        });
    }

    public List<ConsumerGroupInfoDTO> listarConsumerGroups() {
        return conAdmin("listar consumer groups", admin -> {
            List<String> ids = admin.listConsumerGroups().all().get().stream()
                    .map(ConsumerGroupListing::groupId).toList();
            if (ids.isEmpty()) return List.<ConsumerGroupInfoDTO>of();
            return admin.describeConsumerGroups(ids).all().get().values().stream()
                    .map(d -> new ConsumerGroupInfoDTO(d.groupId(), d.state().toString(), d.members().size()))
                    .toList();
        });
    }
}