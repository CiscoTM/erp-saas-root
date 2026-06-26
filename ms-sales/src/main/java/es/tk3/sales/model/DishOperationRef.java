package es.tk3.sales.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "dish_operation")
public class DishOperationRef {
    @Id
    private UUID id;
    @Column(name = "price_floor")
    private BigDecimal priceFloor;
    @Column(name = "diners_per_plate")
    private Integer dinersPerPlate;

    public UUID getId() {
        return id;
    }

    public BigDecimal getPriceFloor() {
        return priceFloor;
    }

    public Integer getDinersPerPlate() {
        return dinersPerPlate;
    }
}

