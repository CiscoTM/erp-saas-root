package es.tk3.operations.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.tk3.common.tenant.TenantContext;
import es.tk3.operations.service.DishSyncService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class DishSyncListener {

    private static final Logger logger = Logger.getLogger(DishSyncListener.class.getName());

    private final DishSyncService dishSyncService;
    private final ObjectMapper mapper;

    public DishSyncListener(DishSyncService dishSyncService, ObjectMapper mapper) {
        this.dishSyncService = dishSyncService;
        this.mapper = mapper;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.dish-events}",
            groupId = "${app.kafka.groups.dish-sync}"
    )
    public void consumeDishEvent(String rawMessage) {
        try {
            JsonNode root = mapper.readTree(rawMessage);

            if(root.has("tenantId")){
                String tenantId = root.get("tenantId").asText();
                TenantContext.setTenantId(tenantId);
            } else {
                logger.warning("Mensaje de Dish descartado: No contiene tenantId");
                return;
            }
            dishSyncService.syncDishReference(root);

        } catch (JsonProcessingException e) {
            logger.severe("Error al procesar evento de plato: " + e.getMessage());
        } finally {
            TenantContext.clear();
        }
    }
}
