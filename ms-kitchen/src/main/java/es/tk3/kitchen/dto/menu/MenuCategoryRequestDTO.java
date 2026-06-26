package es.tk3.kitchen.dto.menu;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class MenuCategoryRequestDTO {
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String name;

    private List<Long> fixedDishIds;
    private List<Long> optionalDishIds;
    private Integer sequenceOrder;
    private Integer selectableCount;
    private String categoryName;

    public MenuCategoryRequestDTO() {}

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getFixedDishIds() {
        return fixedDishIds;
    }
    public void setFixedDishIds(List<Long> fixedDishIds) {
        this.fixedDishIds = fixedDishIds;
    }

    public List<Long> getOptionalDishIds() {
        return optionalDishIds;
    }
    public void setOptionalDishIds(List<Long> optionalDishIds) {
        this.optionalDishIds = optionalDishIds;
    }

    public Integer getSequenceOrder() { return sequenceOrder; }
    public void setSequenceOrder(Integer sequenceOrder) { this.sequenceOrder = sequenceOrder; }

    public Integer getSelectableCount() { return selectableCount; }
    public void setSelectableCount(Integer selectableCount) { this.selectableCount = selectableCount; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}