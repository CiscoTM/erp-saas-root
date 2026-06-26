package es.tk3.operations.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public class FunctionSheetDetailRequest {

    @NotNull(message = "El ID del plato es obligatorio")
    private UUID dishId;

    private String customName;

    @Positive(message = "El precio debe ser positivo")
    private BigDecimal finalPrice;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive
    private Integer quantity;

    public FunctionSheetDetailRequest() {}

    // Getters y Setters
    public UUID getDishId() { return dishId; }
    public void setDishId(UUID dishId) { this.dishId = dishId; }

    public String getCustomName() { return customName; }
    public void setCustomName(String customName) { this.customName = customName; }

    public BigDecimal getFinalPrice() { return finalPrice; }
    public void setFinalPrice(BigDecimal finalPrice) { this.finalPrice = finalPrice; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}