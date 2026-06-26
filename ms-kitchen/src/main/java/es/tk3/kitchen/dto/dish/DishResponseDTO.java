package es.tk3.kitchen.dto.dish;

import java.math.BigDecimal;
import java.util.List;

public class DishResponseDTO {
    private Long id;
    private String technicalName;
    private List<DishPreparationDTO> preparations;
    private BigDecimal baseCost;

    public DishResponseDTO() {}

    public DishResponseDTO(String technicalName, List<DishPreparationDTO> preparations, BigDecimal baseCost) {
        this.technicalName = technicalName;
        this.preparations = preparations;
        this.baseCost = baseCost;
    }

    public String getTechnicalName() { return technicalName; }
    public void setTechnicalName(String technicalName) { this.technicalName = technicalName; }

    public List<DishPreparationDTO> getPreparations() { return preparations; }
    public void setPreparations(List<DishPreparationDTO> preparations) { this.preparations = preparations; }

    public BigDecimal getBaseCost() { return baseCost; }
    public void setBaseCost(BigDecimal baseCost) { this.baseCost = baseCost; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}