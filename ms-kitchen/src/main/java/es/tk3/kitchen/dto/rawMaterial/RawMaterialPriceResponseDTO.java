package es.tk3.kitchen.dto.rawMaterial;

import java.math.BigDecimal;

public class RawMaterialPriceResponseDTO {
    private BigDecimal price;
    private String kitchenUnit;
    private String message;

    public RawMaterialPriceResponseDTO() {}

    public RawMaterialPriceResponseDTO(BigDecimal price, String kitchenUnit, String message) {
        this.price = price;
        this.kitchenUnit = kitchenUnit;
        this.message = message;
    }

    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public String getKitchenUnit() { return kitchenUnit; }
    public void setKitchenUnit(String kitchenUnit) { this.kitchenUnit = kitchenUnit; }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
