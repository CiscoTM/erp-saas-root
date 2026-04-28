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

    // Inyección por constructor (Práctica recomendada)
    public TenantPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publica el evento de creación de un nuevo tenant.
     * @param tenantId Identificador único del tenant (ej: barpaco)
     * @param adminUsername Nombre de usuario del administrador (ej: administrador)
     * @param adminPass Contraseña en texto claro (se hasheará en el destino)
     * @param dbUrl URL de conexión a la base de datos física
     * @param dbUser Usuario de la base de datos
     * @param dbPass Contraseña de la base de datos
     */
    public void publishTenantCreated(String tenantId, String adminUsername, String adminPass,
                                     String dbUrl, String dbUser, String dbPass) {

        // Creamos el evento asegurando que adminPass llega correctamente
        TenantEvent event = new TenantEvent(
                "TENANT_CREATED",
                tenantId,
                adminUsername,
                adminPass,    // 👈 Asegúrate de que tu constructor en TenantEvent reciba esto aquí
                dbUrl,
                dbUser,
                dbPass
        );

        log.info("📡 Publicando evento TENANT_CREATED para: {} (Admin: {})", tenantId, adminUsername);

        try {
            // Enviamos el evento usando el tenantId como clave (Key)
            // Esto garantiza que todos los eventos del mismo bar vayan a la misma partición
            kafkaTemplate.send("tenant-creation-events", tenantId, event);

            log.info("✅ Evento enviado a Kafka satisfactoriamente");
        } catch (Exception e) {
            log.error("❌ Error enviando evento a Kafka para el tenant {}: {}", tenantId, e.getMessage());
            throw e;
        }
    }
}