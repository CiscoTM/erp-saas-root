package es.tk3.operations.service;

import es.tk3.operations.dto.MenuTemplateSyncEventDTO;
import es.tk3.operations.model.Dish;
import es.tk3.operations.repository.DishReferenceRepository;
import es.tk3.operations.repository.OperationalParameterRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PricingEngine {
    private final OperationalParameterRepository parameterRepository;
    private final DishReferenceRepository dishRepo;

    public PricingEngine(OperationalParameterRepository parameterRepository, DishReferenceRepository dishRepo) {
        this.parameterRepository = parameterRepository;
        this.dishRepo = dishRepo;
    }

    public BigDecimal suggestOfficialRoof(MenuTemplateSyncEventDTO event, String tenantId){
        var params = parameterRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new IllegalStateException("Configuración financiera no encontrada para el tenant"));

        BigDecimal worstCaseCost = BigDecimal.ZERO;

        for(MenuTemplateSyncEventDTO.CategoryPayload cat : event.getCategories()){
            if(cat.getFixedDishIds() != null){
                for(Long kitchenDishId : cat.getFixedDishIds()){
                    worstCaseCost = worstCaseCost.add(getDishCost(kitchenDishId));
                }
            }
            if(cat.getOptionalDishIds() != null && !cat.getOptionalDishIds().isEmpty()){
                BigDecimal maxOptionCost = cat.getOptionalDishIds().stream()
                        .map(this::getDishCost)
                        .max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);
                worstCaseCost = worstCaseCost.add(maxOptionCost);
            }
        }
        BigDecimal overheadMultiplier = BigDecimal.ONE.add(params.getOverheadPercentage());
        BigDecimal totalOperationalCost = worstCaseCost.multiply(overheadMultiplier);
        BigDecimal marginDenominator = BigDecimal.ONE.subtract(params.getOptimalMarginThreshold());

        return totalOperationalCost.divide(marginDenominator, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateMenuAutomatedCost(MenuTemplateSyncEventDTO event, String tenantId){
        var params = parameterRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new IllegalStateException("Configuración financiera no encontrada para el tenant"));

        BigDecimal totalMateriaPrima = event.getCategories().stream()
                .filter(cat -> cat.getFixedDishIds() != null)
                .flatMap(cat -> cat.getFixedDishIds().stream())
                .map(this::getDishCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal overHead = BigDecimal.ONE.add(params.getOverheadPercentage());
        return totalMateriaPrima.multiply(overHead).setScale(2, RoundingMode.HALF_UP);

    }
    private BigDecimal getDishCost(Long kitchenDishId){
        return dishRepo.findByKitchenDishId(kitchenDishId).filter(dish -> dish.getBaseCost() != null).map(Dish::getBaseCost).orElse(BigDecimal.ZERO);
    }
}
