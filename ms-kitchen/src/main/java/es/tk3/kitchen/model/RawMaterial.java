package es.tk3.kitchen.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import es.tk3.kitchen.enums.PresenceType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "raw_materials")
public class RawMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "purchase_unit", nullable = false)
    private String purchaseUnit; // Ej: "CAJA_3X5L", "SACO_25KG"

    @Column(name = "purchase_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal purchasePrice; // Ej: 90.00 € o 30.00 €

    @Column(name = "conversion_factor", nullable = false, precision = 10, scale = 4)
    private BigDecimal conversionFactor; // Ej: 15.0000 o 25.0000

    @Column(name = "kitchen_unit", nullable = false)
    private String kitchenUnit;

    @Column(name = "cost_per_kitchen_unit", nullable = false, precision = 10, scale = 4)
    private BigDecimal costPerKitchenUnit;

    @OneToMany(mappedBy = "rawMaterial", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<RawMaterialAllergen> allergens = new HashSet<>();

    public RawMaterial() {}

    @PrePersist
    @PreUpdate
    public void calculateInternalCost(){
        if(this.purchasePrice != null && this.conversionFactor != null && this.conversionFactor.compareTo(BigDecimal.ZERO) > 0){
            this.costPerKitchenUnit = this.purchasePrice.divide(this.conversionFactor, 4, RoundingMode.HALF_UP);
        } else {
            this.costPerKitchenUnit = BigDecimal.ZERO;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public BigDecimal getCostPerKitchenUnit() { return costPerKitchenUnit; }

    public Set<RawMaterialAllergen> getAllergens() { return allergens; }
    public void setAllergens(Set<RawMaterialAllergen> allergens) { this.allergens = allergens; }

    public void addAllergen(Allergen allergen, PresenceType presenceType) {
        RawMaterialAllergen rawMaterialAllergen = new RawMaterialAllergen(this, allergen, presenceType);
        this.allergens.add(rawMaterialAllergen);
    }
}