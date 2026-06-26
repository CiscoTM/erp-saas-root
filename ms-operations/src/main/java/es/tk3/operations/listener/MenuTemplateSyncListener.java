package es.tk3.operations.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.tk3.common.tenant.TenantContext;
import es.tk3.operations.dto.MenuTemplateSyncEventDTO;
import es.tk3.operations.service.MenuTemplateSyncService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class MenuTemplateSyncListener {
    private static final Logger logger = Logger.getLogger(MenuTemplateSyncListener.class.getName());

    private final MenuTemplateSyncService menuTemplateSyncService;
    private final ObjectMapper mapper;

    public MenuTemplateSyncListener(MenuTemplateSyncService menuTemplateSyncService, ObjectMapper mapper) {
        this.menuTemplateSyncService = menuTemplateSyncService;
        this.mapper = mapper;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.menu-templates-sync}",
            groupId = "${app.kafka.groups.operations-template-group}"
    )
    public void consumeTemplateEvent(String rawMessage){
        try {
            MenuTemplateSyncEventDTO event = mapper.readValue(rawMessage, MenuTemplateSyncEventDTO.class);

            if(event.getTenantId() != null){
                TenantContext.setTenantId(event.getTenantId());
            } else {
                logger.warning("Mensaje de MenuTemplate descartado: No contiene tenantId");
                return;
            }

            menuTemplateSyncService.syncTemplateReference(event);

        } catch (Exception e){
            logger.severe("Error al procesar evento: " + e.getMessage());
        } finally {
            TenantContext.clear();
        }
    }
}
