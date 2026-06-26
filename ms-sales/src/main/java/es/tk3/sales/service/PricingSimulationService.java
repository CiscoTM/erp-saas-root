package es.tk3.sales.service;

import es.tk3.common.tenant.TenantContext;
import es.tk3.sales.dto.BookingLineDTO;
import es.tk3.sales.dto.PricingCorridorDTO;
import es.tk3.sales.model.CommercialMenuRef;
import es.tk3.sales.model.DishOperationRef;
import es.tk3.sales.model.OperationalParameterRef;
import es.tk3.sales.repository.CommercialMenuRefRepository;
import es.tk3.sales.repository.DishOperationRefRepository;
import es.tk3.sales.repository.OperationalParameterRefRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;

@Service
public class PricingSimulationService {
    private final DishOperationRefRepository dishRepository;
    private final CommercialMenuRefRepository menuRefRepo;
    private final OperationalParameterRefRepository paramRepository;

    public PricingSimulationService(DishOperationRefRepository dishRepository,
                                    CommercialMenuRefRepository menuRefRepo,
                                    OperationalParameterRefRepository paramRepository) {
        this.dishRepository = dishRepository;
        this.menuRefRepo = menuRefRepo;
        this.paramRepository = paramRepository;
    }

    public PricingCorridorDTO simulatePricing(BookingLineDTO line) {
        int totalPax = line.getPaxCount() != null ? line.getPaxCount() : 1;
        UUID menuId = UUID.fromString(line.getCommercialMenuId());

        CommercialMenuRef menu = menuRefRepo.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("El menú comercial seleccionado no existe en el catálogo"));

        BigDecimal basePricePerPax = menu.getBasePriceFloor() != null ? menu.getBasePriceFloor() : BigDecimal.ZERO;
        BigDecimal totalFloor = basePricePerPax.multiply(BigDecimal.valueOf(totalPax));

        if (line.getDishSelections() != null && !line.getDishSelections().isEmpty()) {
            for (Map.Entry<String, Integer> entry : line.getDishSelections().entrySet()) {
                DishOperationRef dish = dishRepository.findById(UUID.fromString(entry.getKey()))
                        .orElseThrow(() -> new IllegalArgumentException("Plato opcional no encontrado en catálogo"));

                BigDecimal costForSelection = getBigDecimal(entry, dish);
                totalFloor = totalFloor.add(costForSelection);
            }
        }

        BigDecimal minimumPerPax = totalFloor.divide(BigDecimal.valueOf(totalPax), 2, RoundingMode.HALF_UP);

        BigDecimal recommendedPrice = menu.getSuggestedOfficialPrice() != null ? menu.getSuggestedOfficialPrice() : BigDecimal.ZERO;

        BigDecimal appliedOverhead = line.getSnapshotOverheadPercentage();
        BigDecimal appliedRiskMargin = line.getSnapshotRiskMarginThreshold();
        BigDecimal appliedOptimalMargin = line.getSnapshotOptimalMarginThreshold();

        if(appliedOverhead == null || appliedRiskMargin == null || appliedOptimalMargin == null){
            String tenantId = TenantContext.getTenantId();
            OperationalParameterRef currentParams = paramRepository.findById(tenantId).orElse(null);

            if(currentParams != null){
                appliedOverhead = appliedOverhead != null ? appliedOverhead : currentParams.getOverheadPercentage();
                appliedRiskMargin = appliedRiskMargin != null ? appliedRiskMargin : currentParams.getRiskMarginThreshold();
                appliedOptimalMargin = appliedOptimalMargin != null ? appliedOptimalMargin : currentParams.getOptimalMarginThreshold();
            }
        }

        minimumPerPax = minimumPerPax.multiply(BigDecimal.ONE.add(appliedOverhead != null ? appliedOverhead : BigDecimal.ZERO));

        BigDecimal suggestedPrice;
        String policy;
        if (totalPax >= 200) {
            suggestedPrice = minimumPerPax.add(new BigDecimal("5.00"));
            policy = "GRAN_VOLUMEN_DESCUENTO_MAXIMO";
        } else {
            suggestedPrice = minimumPerPax.multiply(BigDecimal.ONE.add(appliedOptimalMargin != null ? appliedOptimalMargin : new BigDecimal("0.20")));
            policy = "TARIFA_OPTIMIZADA_OPERACIONES";
        }

        return new PricingCorridorDTO(
                applyCustomRounding(minimumPerPax),
                recommendedPrice.setScale(2, RoundingMode.HALF_UP),
                applyCustomRounding(suggestedPrice),
                policy,
                appliedOverhead,
                appliedRiskMargin,
                appliedOptimalMargin
        );
    }

    private static @NonNull BigDecimal getBigDecimal(Map.Entry<String, Integer> entry, DishOperationRef dish) {
        BigDecimal requestedQuantity = BigDecimal.valueOf(entry.getValue());
        BigDecimal dinersPerPlate = (dish.getDinersPerPlate() != null && dish.getDinersPerPlate() > 0)
                ? BigDecimal.valueOf(dish.getDinersPerPlate())
                : BigDecimal.ONE;

        BigDecimal platesActuallyNeeded = requestedQuantity.divide(dinersPerPlate, 0, RoundingMode.CEILING);
        return dish.getPriceFloor().multiply(platesActuallyNeeded);
    }
    private BigDecimal applyCustomRounding(BigDecimal valor){
        BigDecimal evaluateValue = valor.setScale(1, RoundingMode.HALF_UP);
        BigDecimal decimal = evaluateValue.remainder(BigDecimal.ONE);

        if(decimal.compareTo(new BigDecimal("0.5")) <= 0 ){
            return valor.setScale(0, RoundingMode.DOWN);
        } else {
            return valor.setScale(0, RoundingMode.UP);
        }

    }
}
