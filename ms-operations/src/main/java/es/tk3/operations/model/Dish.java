package es.tk3.operations.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dish_operation")
public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    @Column(name = "kitchen_dish_id", nullable = false, unique = true)
    private Long kitchenDishId;

    @Column(name = "base_cost")
    private BigDecimal baseCost;
    private String allergens;
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
    @Column(name = "price_floor")
    private BigDecimal priceFloor = BigDecimal.ZERO;
    @Column(name = "diners_per_plate")
    private Integer dinersPerPlate;

    public Dish() {}

    public Dish(UUID id, String name, Long kitchenDishId, BigDecimal baseCost, String allergens, LocalDateTime lastUpdate) {
        this.id = id;
        this.name = name;
        this.kitchenDishId = kitchenDishId;
        this.baseCost = baseCost;
        this.allergens = allergens;
        this.lastUpdate = lastUpdate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getKitchenDishId() {
        return kitchenDishId;
    }

    public void setKitchenDishId(Long kitchenDishId) {
        this.kitchenDishId = kitchenDishId;
    }

    public BigDecimal getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(BigDecimal baseCost) {
        this.baseCost = baseCost;
    }

    public String getAllergens() {
        return allergens;
    }

    public void setAllergens(String allergens) {
        this.allergens = allergens;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public BigDecimal getPriceFloor() {
        return priceFloor;
    }

    public void setPriceFloor(BigDecimal priceFloor) {
        this.priceFloor = priceFloor;
    }

    public Integer getDinersPerPlate() { return dinersPerPlate; }

    public void setDinersPerPlate(Integer dinersPerPlate) { this.dinersPerPlate = dinersPerPlate; }
}
