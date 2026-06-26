package es.tk3.kitchen.listener;

import com.fasterxml.jackson.databind.ObjectMapper;import es.tk3.kitchen.dto.event.RawMaterialPriceUpdatedEvent;
import es.tk3.kitchen.service.RawMaterialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import es.tk3.common.tenant.TenantContext;

@Component
public class RawMaterialPriceUpdatedListener {
    private static final Logger log = LoggerFactory.getLogger(RawMaterialPriceUpdatedListener.class);

    private final RawMaterialService materialService;
    private final ObjectMapper objectMapper;

    public RawMaterialPriceUpdatedListener(
            RawMaterialService materialService, ObjectMapper objectMapper) {
        this.materialService = materialService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "kitchen.raw-materials.price-updated", groupId = "kitchen-price-sync-group")
    public void handlePriceUpdate(String payload) {
        try {
            RawMaterialPriceUpdatedEvent event = new ObjectMapper().readValue(payload, RawMaterialPriceUpdatedEvent.class);
            TenantContext.setTenantId(event.tenantId());

            log.info(">>>> Sincronizando costo en ms-kitchen para Insumo ID: {}", event.rawMaterialId());
            materialService.updateRawMaterialPrice(event.rawMaterialId(), event.newPrice());
        } catch (Exception e) {
            log.error("Error crítico procesando el evento de precios. Payload: {}", payload, e);
        } finally {
            TenantContext.clear();
        }
    }


}
