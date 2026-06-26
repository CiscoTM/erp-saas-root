package es.tk3.kitchen.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.tk3.common.tenant.consumer.AbstractTenantProvisioningConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class TenantProvisioningConsumer extends AbstractTenantProvisioningConsumer {
    @Value("${app.tenant.flyway.locations}")
    private String flywayLocations;

   public TenantProvisioningConsumer(ObjectMapper mapper, DataSource dataSource) {
       super(mapper, dataSource);
    }

    @KafkaListener(topics = "core.tenant.events", groupId = "kitchen-tenant-provisioning")
    public void consumeTenantEvent(String payload){
        processTenantProvisioning(payload, flywayLocations,"flyway_schema_history_kitchen", "KITCHEN");
    }
}





















