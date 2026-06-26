package es.tk3.kafka;

import es.tk3.model.TenantEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TenantPublisher {

    private static final Logger log = LoggerFactory.getLogger(TenantPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TenantPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTenantCreated(String tenantId, String adminUsername, String adminPass,
                                     String dbUrl, String dbUser, String dbPass) {

        TenantEvent event = new TenantEvent(
                "TENANT_CREATED",
                tenantId,
                adminUsername,
                adminPass,
                dbUrl,
                dbUser,
                dbPass
        );

        log.info("📡 Publicando evento TENANT_CREATED para: {} (Admin: {})", tenantId, adminUsername);

        try {
            kafkaTemplate.send("tenant-creation-events", tenantId, event);

            log.info("✅ Evento enviado a Kafka satisfactoriamente");
        } catch (Exception e) {
            log.error("❌ Error enviando evento a Kafka para el tenant {}: {}", tenantId, e.getMessage());
            throw e;
        }
    }
}