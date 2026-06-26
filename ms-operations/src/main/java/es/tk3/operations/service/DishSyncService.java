package es.tk3.operations.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.tk3.common.tenant.TenantContext;
import es.tk3.operations.dto.DishSyncEventDTO;
import es.tk3.operations.model.*;
import es.tk3.operations.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class DishSyncService {

    private static final Logger logger = Logger.getLogger(DishSyncService.class.getName());

    private final DishReferenceRepository dishReferenceRepository;
    private final OperationalParameterRepository parameterRepository;
    private final FunctionSheetRepository functionSheetRepository;
    private final FunctionSheetService functionSheetService;
    private final CommercialMenuRecipeRepository menuRecipeRepository;
    private final CommercialMenuRepository commercialMenuRepository;
    private final ObjectMapper mapper;

    public DishSyncService(
            DishReferenceRepository dishReferenceRepository,
            ObjectMapper mapper,
            OperationalParameterRepository parameterRepository,
            FunctionSheetRepository functionSheetRepository,
            FunctionSheetService functionSheetService,
            CommercialMenuRecipeRepository menuRecipeRepository,
            CommercialMenuRepository commercialMenuRepository
    ) {
        this.dishReferenceRepository = dishReferenceRepository;
        this.mapper = mapper;
        this.parameterRepository = parameterRepository;
        this.functionSheetRepository = functionSheetRepository;
        this.functionSheetService = functionSheetService;
        this.commercialMenuRepository = commercialMenuRepository;
        this.menuRecipeRepository = menuRecipeRepository;
    }

    public void syncDishReference(JsonNode rootPayload){
        try {
            DishSyncEventDTO event = mapper.treeToValue(rootPayload, DishSyncEventDTO.class);
            OperationalParameter params = parameterRepository.findFirstByTenantId(TenantContext.getTenantId());

            BigDecimal totalOperationalCost = event.getBaseCost().multiply(BigDecimal.ONE.add(params.getOverheadPercentage()));
            BigDecimal marginDenominator = BigDecimal.ONE.subtract(params.getMinimumProfitMargin());
            BigDecimal priceFloor = totalOperationalCost.divide(marginDenominator, 2, RoundingMode.HALF_UP);

            Dish dish = dishReferenceRepository.findByKitchenDishId(event.getId())
                    .orElse(new Dish());

            if (dish.getKitchenDishId() == null) {
                dish.setKitchenDishId(event.getId());
                dish.setName(event.getName());
            }

            dish.setBaseCost(totalOperationalCost);
            dish.setPriceFloor(priceFloor);

            Integer diners = (event.getDinersPerPlate() != null && event.getDinersPerPlate() > 0)
                    ? event.getDinersPerPlate()
                    : 1;
            dish.setDinersPerPlate(diners);

            logger.info("Guardando plato " + dish.getName() + " con PriceFloor: " + priceFloor + " (Para " + diners + " comensales)");
            dishReferenceRepository.save(dish);

            evaluateMenuProfitability(dish.getId());

            List<FunctionSheet> affectedSheets = functionSheetRepository.findAllActiveSheetsByDishId(dish.getId());
            affectedSheets.forEach(sheet -> functionSheetService.reevaluateSheetProfitability(sheet, params, null));

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void evaluateMenuProfitability(UUID dishId) {
        List<CommercialMenuRecipe> mappingsConPlato = menuRecipeRepository.findByDishId(dishId);

        Set<String> affectedMenuIds = mappingsConPlato.stream()
                .map(CommercialMenuRecipe::getCommercialMenuId)
                .collect(java.util.stream.Collectors.toSet());

        for (String menuIdStr : affectedMenuIds) {
            List<CommercialMenuRecipe> allMenuDishes = menuRecipeRepository.findByCommercialMenuId(menuIdStr);

            BigDecimal currentMenuFloor = BigDecimal.ZERO;
            for (CommercialMenuRecipe mapping : allMenuDishes) {
                BigDecimal dishFloor = mapping.getDish().getPriceFloor() != null
                        ? mapping.getDish().getPriceFloor()
                        : mapping.getDish().getBaseCost();

                int dinersPerPlate = (mapping.getDish().getDinersPerPlate() != null && mapping.getDish().getDinersPerPlate() > 0)
                        ? mapping.getDish().getDinersPerPlate() : 1;

                BigDecimal costPerPaxForDish = dishFloor.divide(BigDecimal.valueOf(dinersPerPlate), 4, RoundingMode.HALF_UP);

                BigDecimal quantity = BigDecimal.valueOf(mapping.getDefaultQuantity());
                currentMenuFloor = currentMenuFloor.add(costPerPaxForDish.multiply(quantity));
            }

            try {
                UUID menuUuid = UUID.fromString(menuIdStr);
                final BigDecimal finalMenuFloor = currentMenuFloor;

                commercialMenuRepository.findById(menuUuid).ifPresent(menu -> {

                    if (menu.getPrices() != null && !menu.getPrices().isEmpty()) {
                        for (es.tk3.operations.model.MenuPrice priceConfig : menu.getPrices()) {
                            BigDecimal officialPrice = priceConfig.getPricePerPax(); // El Techo

                            if (finalMenuFloor.compareTo(officialPrice) > 0) {
                                logger.warning(String.format(
                                        ">>> [ALERTA TEMPRANA INFLACIÓN] Tenant: %s | El menú comercial '%s' ha entrado en riesgo. " +
                                                "Tarifa: %s | Temporada: %s. El Coste Suelo requerido (%s€) ha superado al Precio de Venta Oficial (%s€).",
                                        TenantContext.getTenantId(),
                                        menu.getName(),
                                        priceConfig.getTariff().getName(),
                                        priceConfig.getSeason().getName(),
                                        finalMenuFloor.setScale(2, RoundingMode.HALF_UP),
                                        officialPrice
                                ));
                            }
                        }
                    }
                });
            } catch (IllegalArgumentException e) {
                logger.severe("El commercialMenuId almacenado no es un UUID válido: " + menuIdStr);
            }
        }
    }
}