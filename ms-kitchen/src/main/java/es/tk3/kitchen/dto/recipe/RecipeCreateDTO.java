package es.tk3.kitchen.dto.recipe;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class RecipeCreateDTO {
    private String name;
    private String category;
    private String preparationSteps;
    private Set<Long> allergenIds;
    private BigDecimal baseCost;
    private List<IngredientDTO> ingredients;
    private Double totalYield;
    private String yieldUnit;

    public RecipeCreateDTO() {}

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

    public String getPreparationSteps() {
        return preparationSteps;
    }
    public void setPreparationSteps(String preparationSteps) {
        this.preparationSteps = preparationSteps;
    }

    public Set<Long> getAllergenIds() { return allergenIds; }
    public void setAllergenIds(Set<Long> allergenIds) { this.allergenIds = allergenIds; }

    public BigDecimal getBaseCost() {
        return baseCost;
    }
    public void setBaseCost(BigDecimal baseCost) {
        this.baseCost = baseCost;
    }

    public List<IngredientDTO> getIngredients() {
        return ingredients;
    }
    public void setIngredients(List<IngredientDTO> ingredients) {
        this.ingredients = ingredients;
    }

    public Double getTotalYield() { return totalYield; }
    public void setTotalYield(Double totalYield) { this.totalYield = totalYield; }

    public String getYieldUnit() { return yieldUnit; }
    public void setYieldUnit(String yieldUnit) { this.yieldUnit = yieldUnit; }

    public static class IngredientDTO {
        @JsonAlias({"name", "ingredientName"})
        private String ingredientName;

        private Long rawMaterialId;
        private Long subRecipeId;

        private BigDecimal quantity;
        private String unit;

        public IngredientDTO() {}

        public Long getRawMaterialId() { return rawMaterialId; }
        public void setRawMaterialId(Long id) { this.rawMaterialId = id; }

        public Long getSubRecipeId() { return subRecipeId; }
        public void setSubRecipeId(Long id) { this.subRecipeId = id; }

        public String getIngredientName() { return ingredientName; }
        public void setIngredientName(String name) { this.ingredientName = name; }

        public BigDecimal getQuantity() { return quantity; }
        public void setQuantity(BigDecimal qty) { this.quantity = qty; }

        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
    }
}