package es.tk3.kitchen.dto.recipe;

import es.tk3.kitchen.model.Recipe;
import es.tk3.kitchen.model.Allergen;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

public class RecipeResponseDTO {
    private Long id;
    private String name;
    private String category;
    private String preparationSteps;
    private BigDecimal baseCost;
    private Double totalYield;
    private String yieldUnit;
    private Set<String> allergens; // Guardamos solo los códigos/nombres para la API

    public RecipeResponseDTO(Recipe recipe) {
        this.id = recipe.getId();
        this.name = recipe.getName();
        this.category = recipe.getCategory();
        this.preparationSteps = recipe.getPreparationSteps();
        this.baseCost = recipe.getBaseCost();
        this.totalYield = recipe.getTotalYield();
        this.yieldUnit = recipe.getYieldUnit();

        // Mapeamos de forma segura forzando la extracción del proxy antes de enviar el JSON
        if (recipe.getAllergens() != null) {
            this.allergens = recipe.getAllergens().stream()
                    .map(Allergen::getCode) // o .getName() según prefieras mostrar
                    .collect(Collectors.toSet());
        }
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getPreparationSteps() { return preparationSteps; }
    public BigDecimal getBaseCost() { return baseCost; }
    public Double getTotalYield() { return totalYield; }
    public String getYieldUnit() { return yieldUnit; }
    public Set<String> getAllergens() { return allergens; }
}