package es.tk3.sales.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "comercial_menu")
public class CommercialMenuRef {

    @Id
    private UUID id;
    private String name;
    @Column(name = "base_price_floor")
    private BigDecimal basePriceFloor;
    @Column(name = "suggested_official_price")
    private BigDecimal suggestedOfficialPrice;

    public UUID getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getSuggestedOfficialPrice() { return suggestedOfficialPrice; }

    public BigDecimal getBasePriceFloor() { return basePriceFloor; }
    public void setBasePriceFloor(BigDecimal basePriceFloor) { this.basePriceFloor = basePriceFloor; }
}