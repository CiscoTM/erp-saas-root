package es.tk3.kitchen.service;

import es.tk3.common.outbox.service.OutboxEventService;
import es.tk3.kitchen.dto.recipe.RecipeCreateDTO;
import es.tk3.kitchen.dto.recipe.RecipeSyncEventDTO;
import es.tk3.kitchen.model.*;
import es.tk3.kitchen.repository.DishRepository;
import es.tk3.kitchen.repository.RawMaterialRepository;
import es.tk3.kitchen.repository.RecipeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final DishRepository dishRepository;
    private final OutboxEventService outboxEventService;
    private final RawMaterialRepository rawMaterialRepository;
    private final DishService dishService;
    private final CostCalculatorProxy costCalculatorProxy;

    public RecipeService(
            RecipeRepository recipeRepository,
            OutboxEventService outboxEventService,
            RawMaterialRepository rawMaterialRepository,
            DishRepository dishRepository,
            DishService dishService,
            CostCalculatorProxy costCalculatorProxy
    ) {
        this.recipeRepository = recipeRepository;
        this.outboxEventService = outboxEventService;
        this.rawMaterialRepository = rawMaterialRepository;
        this.dishRepository = dishRepository;
        this.dishService = dishService;
        this.costCalculatorProxy = costCalculatorProxy;
    }

    @Transactional
    public Recipe createRecipe(RecipeCreateDTO dto) {
        Recipe recipe = new Recipe();
        recipe.setName(dto.getName());
        recipe.setCategory(dto.getCategory());
        recipe.setPreparationSteps(dto.getPreparationSteps());
        recipe.setTotalYield(dto.getTotalYield());
        recipe.setBaseCost(dto.getBaseCost() != null ? dto.getBaseCost() : BigDecimal.ZERO);

        BigDecimal totalCost = BigDecimal.ZERO;
        Set<Allergen> consolidatedAllergens = new HashSet<>();

        if (dto.getIngredients() != null) {
            for (RecipeCreateDTO.IngredientDTO ingDto : dto.getIngredients()) {
                RecipeIngredient ingredient = new RecipeIngredient();
                ingredient.setRecipe(recipe);
                ingredient.setQuantity(ingDto.getQuantity());
                ingredient.setUnit(ingDto.getUnit());

                if (ingDto.getRawMaterialId() != null) {
                    RawMaterial rm = rawMaterialRepository.findById(ingDto.getRawMaterialId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Materia prima no encontrada"));
                    ingredient.setRawMaterial(rm);

                    BigDecimal price = costCalculatorProxy.getRawMaterialPrice(rm.getId());
                    totalCost = totalCost.add(ingDto.getQuantity().multiply(price));

                    if (rm.getAllergens() != null) {
                        for (RawMaterialAllergen rma : rm.getAllergens()) {
                            consolidatedAllergens.add(rma.getAllergen());
                        }
                    }
                } else if (ingDto.getSubRecipeId() != null) {
                    Recipe sub = recipeRepository.findById(ingDto.getSubRecipeId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sub-receta no encontrada"));
                    ingredient.setSubRecipe(sub);

                    totalCost = totalCost.add(ingDto.getQuantity().multiply(sub.getBaseCost()));
                    consolidatedAllergens.addAll(sub.getAllergens());
                }
                recipe.getIngredients().add(ingredient);
            }
        }
        if(recipe.getTotalYield() == null || recipe.getTotalYield() == 0){
            recipe.setBaseCost(totalCost);
        } else {
            BigDecimal finalBaseCost = totalCost.divide(BigDecimal.valueOf(recipe.getTotalYield()), RoundingMode.HALF_UP);
            recipe.setBaseCost(finalBaseCost);
        }
        recipe.setTotalYield(dto.getTotalYield());
        recipe.setYieldUnit(dto.getYieldUnit());
        recipe.setAllergens(consolidatedAllergens);

        Recipe savedRecipe = recipeRepository.save(recipe);

        outboxEventService.createAndSaveEvent(
                savedRecipe.getId().toString(),
                "RECIPE",
                "kitchen.recipes.sync",
                "RECIPE_CREATED",
                new RecipeSyncEventDTO(savedRecipe)
        );

        return savedRecipe;
    }

    @Transactional
    public BigDecimal recalculateRecipeCost(Long recipeId){
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receta no encontrada: " + recipeId ));

        BigDecimal totalCost = BigDecimal.ZERO;
        Set<Allergen> consolidatedAllergens = new HashSet<>();

        for (RecipeIngredient ingredient : recipe.getIngredients()){
            BigDecimal ingredientPrice = BigDecimal.ZERO;

            if(ingredient.getRawMaterial() != null ) {
                ingredientPrice = costCalculatorProxy.getRawMaterialPrice(ingredient.getRawMaterial().getId());

                if (ingredient.getRawMaterial().getAllergens() != null) {
                    for (RawMaterialAllergen rma : ingredient.getRawMaterial().getAllergens()) {
                        consolidatedAllergens.add(rma.getAllergen());
                    }
                }
            } else if(ingredient.getSubRecipe() != null){
                ingredientPrice = recalculateRecipeCost(ingredient.getSubRecipe().getId());
                consolidatedAllergens.addAll(ingredient.getSubRecipe().getAllergens());
            }
            totalCost = totalCost.add(ingredientPrice.multiply(ingredient.getQuantity()));
        }

        if(recipe.getTotalYield() == null || recipe.getTotalYield() == 0){
            recipe.setBaseCost(totalCost);
        } else {
            BigDecimal yield = BigDecimal.valueOf(recipe.getTotalYield());
            recipe.setBaseCost(totalCost.divide(yield, 2, RoundingMode.HALF_UP));
        }
        recipe.setAllergens(consolidatedAllergens);
        Recipe updatedRecipe = recipeRepository.save(recipe);

        outboxEventService.createAndSaveEvent(
                updatedRecipe.getId().toString(),
                "RECIPE",
                "kitchen.recipes.sync",
                "RECIPE_UPDATED",
                new RecipeSyncEventDTO(updatedRecipe)
        );

        List<Recipe> parentRecipes = recipeRepository.findRecipesByIngredientSubRecipeId(recipeId);
        for(Recipe parent : parentRecipes){
            this.recalculateRecipeCost(parent.getId());
        }
        List<Dish> dependentDishes = dishRepository.findDishesByPreparationRecipeId(recipeId);
        for(Dish dish : dependentDishes){
            dishService.updateCostAndSync(dish.getId());
        }
        return recipe.getBaseCost();
    }

    @Transactional
    public List<Recipe> recalculateRecipeCostsByMaterial(Long materialId){
        List<Recipe> affectedRecipes = recipeRepository.findRecipesByIngredientRawMaterialId(materialId);

        for(Recipe recipe : affectedRecipes){
            this.recalculateRecipeCost(recipe.getId());
        }
        return affectedRecipes;
    }
}