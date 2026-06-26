package es.tk3.kitchen.dto.rawMaterial;

import es.tk3.kitchen.enums.PresenceType;
import es.tk3.kitchen.model.RawMaterial;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

public class RawMaterialResponseDTO {
    private Long id;
    private String name;

    private String purchaseUnit;
    private BigDecimal purchasePrice;
    private BigDecimal conversionFactor;
    private String kitchenUnit;
    private BigDecimal costPerKitchenUnit;

    private Set<AllergenPresenceDTO> allergens; // Devuelve objeto rico con código y tipo

    public RawMaterialResponseDTO() {}

    public static class AllergenPresenceDTO {
        private String code;
        private PresenceType presenceType;

        public AllergenPresenceDTO(String code, PresenceType presenceType) {
            this.code = code;
            this.presenceType = presenceType;
        }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public PresenceType getPresenceType() { return presenceType; }
        public void setPresenceType(PresenceType presenceType) { this.presenceType = presenceType; }
    }

    public static RawMaterialResponseDTO fromEntity(RawMaterial entity) {
        RawMaterialResponseDTO dto = new RawMaterialResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());

        dto.setPurchaseUnit(entity.getPurchaseUnit());
        dto.setPurchasePrice(entity.getPurchasePrice());
        dto.setConversionFactor(entity.getConversionFactor());
        dto.setKitchenUnit(entity.getKitchenUnit());
        dto.setCostPerKitchenUnit(entity.getCostPerKitchenUnit());

        if (entity.getAllergens() != null) {
            dto.setAllergens(entity.getAllergens().stream()
                    .map(rma -> new AllergenPresenceDTO(
                            rma.getAllergen().getCode(), // Corregido: rma es RawMaterialAllergen, entramos a Allergen
                            rma.getPresenceType()
                    ))
                    .collect(Collectors.toSet()));
        }
        return dto;
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

    public String getPurchaseUnit() {
        return purchaseUnit;
    }

    public void setPurchaseUnit(String purchaseUnit) {
        this.purchaseUnit = purchaseUnit;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getConversionFactor() {
        return conversionFactor;
    }

    public void setConversionFactor(BigDecimal conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    public String getKitchenUnit() {
        return kitchenUnit;
    }

    public void setKitchenUnit(String kitchenUnit) {
        this.kitchenUnit = kitchenUnit;
    }

    public BigDecimal getCostPerKitchenUnit() {
        return costPerKitchenUnit;
    }

    public void setCostPerKitchenUnit(BigDecimal costPerKitchenUnit) {
        this.costPerKitchenUnit = costPerKitchenUnit;
    }

    public Set<AllergenPresenceDTO> getAllergens() {
        return allergens;
    }

    public void setAllergens(Set<AllergenPresenceDTO> allergens) {
        this.allergens = allergens;
    }
}