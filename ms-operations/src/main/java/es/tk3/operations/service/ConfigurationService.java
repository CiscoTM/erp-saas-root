package es.tk3.operations.service;

import es.tk3.common.outbox.service.OutboxEventService;
import es.tk3.common.tenant.TenantContext;
import es.tk3.operations.dto.OperationalParamRequestDTO;
import es.tk3.operations.dto.OperationalParamResponseDTO;
import es.tk3.operations.model.OperationalParameter;
import es.tk3.operations.model.Season;
import es.tk3.operations.model.Tariff;
import es.tk3.operations.repository.OperationalParameterRepository;
import es.tk3.operations.repository.SeasonRepository;
import es.tk3.operations.repository.TariffRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ConfigurationService {
    private final SeasonRepository seasonRepository;
    private final TariffRepository tariffRepository;
    private final OperationalParameterRepository parameterRepository;
    private final OutboxEventService outboxEventService;

    @Value("${app.kafka.topics.parameter-event}")
    private String parameterTopic;

    public ConfigurationService(SeasonRepository seasonRepository,
                                TariffRepository tariffRepository,
                                OperationalParameterRepository parameterRepository,
                                OutboxEventService outboxEventService) {
        this.seasonRepository = seasonRepository;
        this.tariffRepository = tariffRepository;
        this.parameterRepository = parameterRepository;
        this.outboxEventService = outboxEventService;
    }
    @Transactional
    public Season createSeason(Season season) {
        return seasonRepository.save(season);
    }

    @Transactional
    public Tariff createTariff(Tariff tariff) {
        return tariffRepository.save(tariff);
    }

    @Transactional
    public OperationalParameter createParameter(OperationalParameter param) {
        param.setTenantId(TenantContext.getTenantId());
        OperationalParameter saved = parameterRepository.save(param);

        outboxEventService.createAndSaveEvent(
                saved.getId().toString(),
                "OperationalParameter",
                parameterTopic,
                "PARAMETER_UPDATED",
                toDto(saved)
        );
        return saved;
    }

    public OperationalParameter toEntity(OperationalParamRequestDTO dto){
        OperationalParameter param = new OperationalParameter();
        param.setTenantId(dto.getTenantId());
        param.setOptimalMarginThreshold(dto.getOptimalMarginThreshold());
        param.setOverheadPercentage(dto.getOverheadPercentage());
        param.setRiskMarginThreshold(dto.getRiskMarginThreshold());

        param.setMinimumProfitMargin(dto.getMinimumProfitMargin() != null ? dto.getMinimumProfitMargin() : new BigDecimal("0.20"));
        param.setOptimalProfitMargin(dto.getOptimalProfitMargin() != null ? dto.getOptimalProfitMargin() : new BigDecimal("0.75"));
        return param;
    }
    public OperationalParamResponseDTO toDto(OperationalParameter parameter){
        OperationalParamResponseDTO dto = new OperationalParamResponseDTO();
        dto.setOverheadPercentage(parameter.getOverheadPercentage());
        dto.setOptimalMarginThreshold(parameter.getOptimalMarginThreshold());
        dto.setRiskMarginThreshold(parameter.getRiskMarginThreshold());

        dto.setMinimumProfitMargin(parameter.getMinimumProfitMargin());
        dto.setOptimalProfitMargin(parameter.getOptimalProfitMargin());
        dto.setTenantId(TenantContext.getTenantId());

        return dto;
    }
    @Transactional
    public OperationalParamResponseDTO partialUpdateParameter(Long id, OperationalParamRequestDTO dto) {
        OperationalParameter existing = parameterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Parámetro operativo no encontrado con ID: " + id));

        if (dto.getOverheadPercentage() != null) {
            existing.setOverheadPercentage(dto.getOverheadPercentage());
        }
        if (dto.getRiskMarginThreshold() != null) {
            existing.setRiskMarginThreshold(dto.getRiskMarginThreshold());
        }
        if (dto.getOptimalMarginThreshold() != null) {
            existing.setOptimalMarginThreshold(dto.getOptimalMarginThreshold());
        }
        if (dto.getMinimumProfitMargin() != null) {
            existing.setMinimumProfitMargin(dto.getMinimumProfitMargin());
        }
        if (dto.getOptimalProfitMargin() != null) {
            existing.setOptimalProfitMargin(dto.getOptimalProfitMargin());
        }

        OperationalParameter updated = parameterRepository.save(existing);

        outboxEventService.createAndSaveEvent(
                updated.getId().toString(),
                "OperationalParameter",
                parameterTopic,
                "PARAMETER_UPDATED",
                toDto(updated)
        );

        return toDto(updated);
    }
}
