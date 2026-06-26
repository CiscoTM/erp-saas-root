package es.tk3.sales.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.tk3.common.tenant.TenantContext;
import es.tk3.sales.model.OperationalParameterRef;
import es.tk3.sales.repository.OperationalParameterRefRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OperationalParamSyncListener {
    private final OperationalParameterRefRepository refRepository;
    private final ObjectMapper mapper;

    public OperationalParamSyncListener(
            OperationalParameterRefRepository refRepository,
            ObjectMapper mapper
    ) {
        this.refRepository = refRepository;
        this.mapper = mapper;
    }
    @KafkaListener(
            topics = "${app.kafka.topics.parameter-event}",
            groupId = "${app.kafka.groups.config-sync}"
    )
    public void consumeParamUpdate(String rawMessage){
        try {
            JsonNode root = mapper.readTree(rawMessage);
            String tenantId = root.get("tenantId").asText();
            TenantContext.setTenantId(tenantId);

            OperationalParameterRef ref = refRepository.findById(tenantId).orElse(new OperationalParameterRef());
            ref.setTenantId(tenantId);
            ref.setOverheadPercentage(new BigDecimal(root.get("overheadPercentage").asText()));
            ref.setRiskMarginThreshold(new BigDecimal(root.get("riskMarginThreshold").asText()));
            ref.setOptimalMarginThreshold(new BigDecimal(root.get("optimalMarginThreshold").asText()));

            refRepository.save(ref);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error crítico al serializar el payload para el módulo", e);
        } finally {
            TenantContext.clear();
        }
    }
}
