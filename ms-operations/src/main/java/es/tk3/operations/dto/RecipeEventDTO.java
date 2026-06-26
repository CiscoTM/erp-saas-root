package es.tk3.operations.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class RecipeEventDTO {
    private String tenantId;
    private Long kitchenDishId;
    private String name;
    private String category;
    private String allergens;
    private BigDecimal basePrice;
    private String eventType;

    public RecipeEventDTO() {}

    public RecipeEventDTO(String tenantId, Long kitchenDishId, String name, String category, String allergens, BigDecimal basePrice, String eventType) {
        this.tenantId = tenantId;
        this.kitchenDishId = kitchenDishId;
        this.name = name;
        this.category = category;
        this.allergens = allergens;
        this.basePrice = basePrice;
        this.eventType = eventType;
    }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public Long getKitchenDishId() { return kitchenDishId; }
    public void setKitchenDishId(Long kitchenDishId) { this.kitchenDishId = kitchenDishId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getAllergens() { return allergens; }
    public void setAllergens(String allergens) { this.allergens = allergens; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeEventDTO that = (RecipeEventDTO) o;
        return Objects.equals(tenantId, that.tenantId) && Objects.equals(kitchenDishId, that.kitchenDishId) && Objects.equals(name, that.name) && Objects.equals(category, that.category) && Objects.equals(allergens, that.allergens) && Objects.equals(basePrice, that.basePrice) && Objects.equals(eventType, that.eventType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, kitchenDishId, name, category, allergens, basePrice, eventType);
    }

    @Override
    public String toString() {
        return "RecipeEventDTO{" + "tenantId='" + tenantId + '\'' + ", kitchenDishId=" + kitchenDishId + ", name='" + name + '\'' + ", category='" + category + '\'' + ", allergens='" + allergens + '\'' + ", basePrice=" + basePrice + ", eventType='" + eventType + '\'' + '}';
    }

    public static RecipeEventDTOBuilder builder() {
        return new RecipeEventDTOBuilder();
    }

    public static class RecipeEventDTOBuilder {
        private String tenantId;
        private Long kitchenDishId;
        private String name;
        private String category;
        private String allergens;
        private BigDecimal basePrice;
        private String eventType;

        RecipeEventDTOBuilder() {}

        public RecipeEventDTOBuilder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public RecipeEventDTOBuilder kitchenDishId(Long kitchenDishId) { this.kitchenDishId = kitchenDishId; return this; }
        public RecipeEventDTOBuilder name(String name) { this.name = name; return this; }
        public RecipeEventDTOBuilder category(String category) { this.category = category; return this; }
        public RecipeEventDTOBuilder allergens(String allergens) { this.allergens = allergens; return this; }
        public RecipeEventDTOBuilder basePrice(BigDecimal basePrice) { this.basePrice = basePrice; return this; }
        public RecipeEventDTOBuilder eventType(String eventType) { this.eventType = eventType; return this; }

        public RecipeEventDTO build() {
            return new RecipeEventDTO(tenantId, kitchenDishId, name, category, allergens, basePrice, eventType);
        }
    }
}

