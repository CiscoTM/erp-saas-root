package es.tk3.kitchen.dto.dish;

import java.math.BigDecimal;
import java.util.Set;

public class DishSyncEventDTO {

    private Long id;
    private String name;
    private Set<String> allergenCodes;
    private BigDecimal baseCost;
    private String tenantId;
    private Integer dinersPerPlate;

    public DishSyncEventDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Set<String> getAllergenCodes() { return allergenCodes; }
    public void setAllergenCodes(Set<String> allergenCodes) { this.allergenCodes = allergenCodes; }

    public BigDecimal getBaseCost() { return baseCost; }
    public void setBaseCost(BigDecimal baseCost) { this.baseCost = baseCost; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public Integer getDinersPerPlate() {
        return dinersPerPlate;
    }

    public void setDinersPerPlate(Integer dinersPerPlate) {
        this.dinersPerPlate = dinersPerPlate;
    }
}
