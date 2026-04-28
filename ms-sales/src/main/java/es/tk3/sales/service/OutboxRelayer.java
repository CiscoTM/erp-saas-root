package es.tk3.sales.service;

import es.tk3.common.tenant.DynamicRoutingDataSource;
import es.tk3.common.tenant.TenantContext;
import es.tk3.sales.model.Outbox;
import es.tk3.sales.repository.OutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Service
public class OutboxRelayer {

    private static final Logger log = LoggerFactory.getLogger(OutboxRelayer.class);

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final DataSource dataSource;

    public OutboxRelayer(OutboxRepository outboxRepository,
                         KafkaTemplate<String, String> kafkaTemplate,
                         DataSource dataSource) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.dataSource = dataSource;
    }

    @Scheduled(fixedDelay = 10000) // Aumentamos a 10s para pruebas
    public void processOutboxAllTenants() {
        try {
            // 1. Obtenemos los IDs de todos los tenants cargados
            DynamicRoutingDataSource routingDS = dataSource.unwrap(DynamicRoutingDataSource.class);
            Set<String> tenantIds = routingDS.getTenantIds(); // Asegúrate de tener este getter en tu DynamicRoutingDataSource

            if (tenantIds.isEmpty()) return;

            for (String tenantId : tenantIds) {
                processForTenant(tenantId);
            }
        } catch (SQLException e) {
            log.error("Error accediendo al DynamicRoutingDataSource", e);
        }
    }

    private void processForTenant(String tenantId) {
        try {
            // 2. Activamos el contexto del Tenant para este hilo
            TenantContext.setTenantId(tenantId);

            List<Outbox> pendingMessages = outboxRepository.findByStatus("PENDING");

            if (!pendingMessages.isEmpty()) {
                log.info("Procesando {} mensajes para el tenant: {}", pendingMessages.size(), tenantId);

                for (Outbox message : pendingMessages) {
                    sendToKafka(message);
                }
            }
        } catch (Exception e) {
            log.error("Error procesando outbox para tenant {}", tenantId, e);
        } finally {
            // 3. ¡CRUCIAL! Limpiamos el contexto para el siguiente tenant
            TenantContext.clear();
        }
    }

    private void sendToKafka(Outbox message) {
        // El topic ahora es genérico para todo el dominio de ventas
        String topic = "sales.bookings";

        // Usamos el tenantId como clave (Partition Key) para asegurar el orden por cliente
        kafkaTemplate.send(topic, message.getTenantId(), message.getPayload())
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        updateMessageStatus(message.getId(), "PROCESSED", message.getTenantId());
                    } else {
                        log.error("Error enviando a Kafka", ex);
                    }
                });
    }

    private void updateMessageStatus(java.util.UUID id, String status, String tenantId) {
        try {
            TenantContext.setTenantId(tenantId);
            outboxRepository.findById(id).ifPresent(m -> {
                m.setStatus(status);
                outboxRepository.save(m);
            });
        } finally {
            TenantContext.clear();
        }
    }
}