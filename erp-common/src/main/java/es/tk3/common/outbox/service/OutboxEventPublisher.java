package es.tk3.common.outbox.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.tk3.common.outbox.model.OutboxEvent;
import es.tk3.common.outbox.repository.OutboxEventRepository;
import es.tk3.common.tenant.DynamicRoutingDataSource;
import es.tk3.common.tenant.TenantContext;
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
public class OutboxEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxEventPublisher.class);

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final DataSource dataSource;
    private final ObjectMapper mapper;

    public OutboxEventPublisher(
            OutboxEventRepository outboxEventRepository,
            KafkaTemplate<String, Object> kafkaTemplate,
            DataSource dataSource,
            ObjectMapper mapper
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.dataSource = dataSource;
        this.mapper = mapper;
    }

    @Scheduled(fixedDelayString = "${app.outbox.delay:5000}")
    public void processOutboxAllTenants(){
        try {
            processForTenant(null);

            DynamicRoutingDataSource routingDS = dataSource.unwrap(DynamicRoutingDataSource.class);
            Set<String> tenantsIds = routingDS.getTenantIds();

            if(tenantsIds == null || tenantsIds.isEmpty()) return;

            for(String tenantId : tenantsIds){
                processForTenant(tenantId);
            }

        } catch (SQLException e) {
            log.error("Error accediendo al DynamicRoutingDataSource", e);
        }
    }

    private void processForTenant(String tenantId){
        try {
            TenantContext.setTenantId(tenantId);

            List<OutboxEvent> pendingMessages = outboxEventRepository.findTop20ByStatusOrderByCreatedAtAsc("PENDING");

            if(!pendingMessages.isEmpty()){
                log.info(">>>> [OUTBOX] Procesando {} eventos pendientes para el tenant: {}", pendingMessages.size(), tenantId);
                for(OutboxEvent event : pendingMessages){
                    sendToKafkaSynchronously(event, tenantId);
                }
            }
        } catch (Exception e) {
            log.error("Error procesando outbox para tenant {}", tenantId, e);
        } finally {
            TenantContext.clear();
        }
    }

    private void sendToKafkaSynchronously(OutboxEvent event, String tenantId){
        try {
            JsonNode payloadNode = mapper.readTree(event.getPayload());

            kafkaTemplate.send(event.getTopic(), event.getAggregateId(), payloadNode).get();

            event.setStatus("PROCESSED");
            outboxEventRepository.save(event);

            log.info("✅ [OUTBOX] Evento {} enviado a Kafka y marcado como PROCESSED para el tenant {}", event.getId(), tenantId);

        } catch (Exception e) {
            log.error("❌ [OUTBOX] Error enviando a Kafka o actualizando estado para el evento {}", event.getId(), e);
        }
    }
}