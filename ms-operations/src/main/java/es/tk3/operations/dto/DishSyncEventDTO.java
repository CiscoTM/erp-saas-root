package es.tk3.operations.dto;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public class DishSyncEventDTO {

    private String tenantId;
    private Long id;
    private String name;
    private Set<String> allergenCodes;
    private BigDecimal baseCost;
    private Integer dinersPerPlate;

    public DishSyncEventDTO() {}

    public Integer getDinersPerPlate() { return dinersPerPlate; }
    public void setDinersPerPlate(Integer dinersPerPlate) { this.dinersPerPlate = dinersPerPlate; }

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
}
