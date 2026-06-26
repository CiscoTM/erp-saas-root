package es.tk3.kitchen.dto.recipe;

import es.tk3.kitchen.model.Allergen;
import es.tk3.kitchen.model.Recipe;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeSyncEventDTO {
    private Long id;
    private String name;
    private String category;
    private BigDecimal baseCost;
    private List<String> allergenCodes; // Lista plana de códigos

    public RecipeSyncEventDTO(Recipe recipe) {
        this.id = recipe.getId();
        this.name = recipe.getName();
        this.category = recipe.getCategory();
        this.baseCost = recipe.getBaseCost();
        this.allergenCodes = recipe.getAllergens().stream()
                .map(Allergen::getCode)
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(BigDecimal baseCost) {
        this.baseCost = baseCost;
    }

    public List<String> getAllergenCodes() {
        return allergenCodes;
    }

    public void setAllergenCodes(List<String> allergenCodes) {
        this.allergenCodes = allergenCodes;
    }
}