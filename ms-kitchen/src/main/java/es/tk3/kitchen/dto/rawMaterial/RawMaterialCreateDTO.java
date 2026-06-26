package es.tk3.kitchen.dto.rawMaterial;

import es.tk3.kitchen.enums.PresenceType;
import java.math.BigDecimal;
import java.util.Set;

public class RawMaterialCreateDTO {
    private String name;
    private String purchaseUnit;
    private BigDecimal purchasePrice;
    private BigDecimal conversionFactor;
    private String kitchenUnit;
    private Set<AllergenInput> allergens;

    public RawMaterialCreateDTO() {}

    public static class AllergenInput {
        private Long id;
        private PresenceType presenceType = PresenceType.CONTAINS; // Por defecto directo

        public AllergenInput() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public PresenceType getPresenceType() { return presenceType; }
        public void setPresenceType(PresenceType presenceType) { this.presenceType = presenceType; }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPurchaseUnit() { return purchaseUnit; }
    public void setPurchaseUnit(String purchaseUnit) { this.purchaseUnit = purchaseUnit; }

    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }

    public BigDecimal getConversionFactor() { return conversionFactor; }
    public void setConversionFactor(BigDecimal conversionFactor) { this.conversionFactor = conversionFactor; }

    public String getKitchenUnit() { return kitchenUnit; }
    public void setKitchenUnit(String kitchenUnit) { this.kitchenUnit = kitchenUnit; }

    public Set<AllergenInput> getAllergens() { return allergens; }
    public void setAllergens(Set<AllergenInput> allergens) { this.allergens = allergens; }
}