package es.tk3.kitchen.service;

import es.tk3.common.outbox.service.OutboxEventService;
import es.tk3.common.tenant.TenantContext;
import es.tk3.kitchen.dto.dish.DishSyncEventDTO;
import es.tk3.kitchen.model.Allergen;
import es.tk3.kitchen.model.Dish;
import es.tk3.kitchen.model.DishPreparation;
import es.tk3.kitchen.repository.DishRepository;
import es.tk3.kitchen.repository.RawMaterialRepository;
import es.tk3.kitchen.repository.RecipeRepository;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DishService {
    private final DishRepository dishRepository;
    private final OutboxEventService outboxEventService;
    private final RecipeRepository recipeRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final CostCalculatorProxy costCalculatorProxy;

    public DishService(DishRepository dishRepository,
                       OutboxEventService outboxEventService,
                       RecipeRepository recipeRepository,
                       RawMaterialRepository rawMaterialRepository,
                       CostCalculatorProxy costCalculatorProxy) {
        this.dishRepository = dishRepository;
        this.outboxEventService = outboxEventService;
        this.recipeRepository = recipeRepository;
        this.rawMaterialRepository = rawMaterialRepository;
        this.costCalculatorProxy = costCalculatorProxy;
    }

    @Transactional
    public Dish saveAndPublish(Dish dish, String eventType) {
        if (dish.getPreparations() != null) {
            for (DishPreparation prep : dish.getPreparations()) {

                prep.setDish(dish);

                if (prep.getRecipe() != null && prep.getRecipe().getId() != null) {
                    prep.setRecipe(recipeRepository.findById(prep.getRecipe().getId())
                            .orElseThrow(() -> new RuntimeException("Receta no encontrada: " + prep.getRecipe().getId())));
                }
                if (prep.getRawMaterial() != null && prep.getRawMaterial().getId() != null) {
                    prep.setRawMaterial(rawMaterialRepository.findById(prep.getRawMaterial().getId())
                            .orElseThrow(() -> new RuntimeException("Materia prima no encontrada: " + prep.getRawMaterial().getId())));
                }
            }
        }
        BigDecimal cost = calculateCost(dish);
        dish.setBaseCost(cost);

        Dish savedDish = dishRepository.save(dish);

        DishSyncEventDTO eventDTO = new DishSyncEventDTO();
        eventDTO.setTenantId(TenantContext.getTenantId());
        eventDTO.setId(savedDish.getId());
        eventDTO.setName(savedDish.getTechnicalName());
        eventDTO.setBaseCost(savedDish.getBaseCost());
        eventDTO.setDinersPerPlate(savedDish.getDinersPerPlate());

        Set<Allergen> cleanAllergens = savedDish.getAllergens().stream()
                .map(a -> {
                    Allergen clean = new Allergen();
                    clean.setId(a.getId());
                    clean.setCode(a.getCode());
                    clean.setName(a.getName());
                    return clean;
                })
                .collect(Collectors.toSet());

        outboxEventService.createAndSaveEvent(
                savedDish.getId().toString(),
                "DISH",
                "kitchen.dishes.sync",
                eventType,
                eventDTO
        );

        return savedDish;
    }

    @Transactional
    public void syncDishToOperations(Long disId){
        Dish dish = dishRepository.findById(disId)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado para sincronización: " + disId));

        DishSyncEventDTO eventDTO = new DishSyncEventDTO();
        eventDTO.setId(dish.getId());
        eventDTO.setName(dish.getTechnicalName());
        eventDTO.setBaseCost(dish.getBaseCost());
        eventDTO.setDinersPerPlate(dish.getDinersPerPlate());

        Set<String> cleanAllergens = dish.getAllergens().stream()
                .map(Allergen::getCode)
                .collect(Collectors.toSet());
        eventDTO.setAllergenCodes(cleanAllergens);

        outboxEventService.createAndSaveEvent(
                dish.getId().toString(),
                "DISH",
                "kitchen.dishes.sync",
                "DISH_FORCE_SYNC",
                eventDTO
        );
    }

    @Transactional
    public void updateCostAndSync(Long dishId) {
        Dish dish = dishRepository.findById(dishId).orElseThrow();

        BigDecimal newTotalCost = dish.getPreparations().stream()
                .map(prep -> {
                    BigDecimal itemCost = BigDecimal.ZERO;
                    BigDecimal qty = BigDecimal.valueOf(prep.getQuantityRequired());
                    if(prep.getRecipe() != null && prep.getRecipe().getBaseCost() != null){
                        itemCost = prep.getRecipe().getBaseCost().multiply(qty);
                    }
                    if(prep.getRawMaterial() != null){
                        BigDecimal price = costCalculatorProxy.getRawMaterialPrice(prep.getRawMaterial().getId());
                        itemCost = itemCost.add(price.multiply(qty));
                    }
                    return itemCost;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        dish.setBaseCost(newTotalCost);
        dishRepository.save(dish);

        this.syncDishToOperations(dishId);
    }

    @Transactional
    public Dish update(Long id, Dish dishDetails) {
        Dish existingDish = dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("El plato: " + id + " no existe."));

        existingDish.setTechnicalName(dishDetails.getTechnicalName());

        existingDish.getPreparations().clear();
        if(dishDetails.getPreparations() != null){
            for (DishPreparation prep : dishDetails.getPreparations()) {
                prep.setDish(existingDish);
                existingDish.getPreparations().add(prep);
            }
        }

        BigDecimal newTotalCost = existingDish.getPreparations().stream()
                .map(prep -> {
                    BigDecimal itemCost = BigDecimal.ZERO;
                    BigDecimal qty = BigDecimal.valueOf(prep.getQuantityRequired());
                    if(prep.getRecipe() != null && prep.getRecipe().getBaseCost() != null)
                        itemCost = prep.getRecipe().getBaseCost().multiply(qty);
                    // ADAPTACIÓN: Cálculo con Proxy
                    if(prep.getRawMaterial() != null){
                        BigDecimal price = costCalculatorProxy.getRawMaterialPrice(prep.getRawMaterial().getId());
                        itemCost = itemCost.add(price.multiply(qty));
                    }
                    return itemCost;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        existingDish.setBaseCost(newTotalCost);

        return this.saveAndPublish(existingDish, "DISH_UPDATED");
    }

    @Transactional(readOnly = true)
    public List<Dish> findAll(){
        return  dishRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Dish findById(Long id){
        return  dishRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("El plato " + id + " no existe")
        );
    }

    private BigDecimal calculateCost(Dish dish) {
        return dish.getPreparations().stream()
                .map(prep -> {
                    BigDecimal itemCost = BigDecimal.ZERO;
                    BigDecimal qty = BigDecimal.valueOf(prep.getQuantityRequired());
                    if (prep.getRecipe() != null && prep.getRecipe().getBaseCost() != null)
                        itemCost = prep.getRecipe().getBaseCost().multiply(qty);
                    // ADAPTACIÓN: Cálculo con Proxy
                    if (prep.getRawMaterial() != null) {
                        BigDecimal price = costCalculatorProxy.getRawMaterialPrice(prep.getRawMaterial().getId());
                        itemCost = itemCost.add(price.multiply(qty));
                    }
                    return itemCost;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}