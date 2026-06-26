package es.tk3.kitchen.dto.menu;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.List;

public class MenuTemplateRequestDTO {
    @NotBlank(message = "El nombre de la plantilla es obligatorio")
    private String name;

    @NotEmpty(message = "La plantilla debe contener al menos una categoría")
    @Valid
    private List<MenuCategoryRequestDTO> categories;

    private BigDecimal basePrice = BigDecimal.ZERO;

    public MenuTemplateRequestDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MenuCategoryRequestDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<MenuCategoryRequestDTO> categories) {
        this.categories = categories;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }
}