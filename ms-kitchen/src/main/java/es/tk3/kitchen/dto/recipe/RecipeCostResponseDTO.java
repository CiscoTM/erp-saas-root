package es.tk3.kitchen.dto.recipe;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RecipeCostResponseDTO {
    private BigDecimal cost;
    private LocalDateTime createdAt  = LocalDateTime.now();;
    private String message;

    public RecipeCostResponseDTO(BigDecimal cost, String message) {
        this.cost = cost;
        this.message = message;
    }

    public RecipeCostResponseDTO() {}

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
