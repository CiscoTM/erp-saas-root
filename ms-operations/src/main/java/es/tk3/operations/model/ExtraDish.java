package es.tk3.operations.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "extra_dish")
public class ExtraDish {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "dish_id", columnDefinition = "id")
    private Dish dishId;

    @Column(name = "commercial_name")
    private String CommercialName;

    @Column(name = "fixed_price")
    private BigDecimal fixedPrice;

    public ExtraDish() {
    }

    public ExtraDish(UUID id, Dish dishId, String commercialName, BigDecimal fixedPrice) {
        this.id = id;
        this.dishId = dishId;
        CommercialName = commercialName;
        this.fixedPrice = fixedPrice;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Dish getDishId() {
        return dishId;
    }

    public void setDishId(Dish dishId) {
        this.dishId = dishId;
    }

    public String getCommercialName() {
        return CommercialName;
    }

    public void setCommercialName(String commercialName) {
        CommercialName = commercialName;
    }

    public BigDecimal getFixedPrice() {
        return fixedPrice;
    }

    public void setFixedPrice(BigDecimal fixedPrice) {
        this.fixedPrice = fixedPrice;
    }
}
