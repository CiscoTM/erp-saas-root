package es.tk3.operations.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "comercial_menu")
public class CommercialMenu {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(name = "menu_template_id", nullable = false)
    private Long menuTemplateId;
    @Column(name = "is_active")
    private Boolean isActive = true;
    @Column(name = "base_price_floor")
    private BigDecimal basePriceFloor = BigDecimal.ZERO;
    @Column(name = "suggested_official_price")
    private BigDecimal suggestedOfficialPrice = BigDecimal.ZERO;

    @OneToMany(mappedBy = "commercialMenu", cascade = CascadeType.ALL)
    private List<MenuPrice> prices;

    public CommercialMenu() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getMenuTemplateId() { return menuTemplateId; }
    public void setMenuTemplateId(Long menuTemplateId) { this.menuTemplateId = menuTemplateId; }
    public List<MenuPrice> getPrices() { return prices; }
    public void setPrices(List<MenuPrice> prices) { this.prices = prices; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description;}
    public Boolean getActive() { return isActive; }
    public void setActive(Boolean active) { isActive = active; }
    public BigDecimal getBasePriceFloor() { return basePriceFloor; }
    public void setBasePriceFloor(BigDecimal basePriceFloor) { this.basePriceFloor = basePriceFloor; }
    public BigDecimal getSuggestedOfficialPrice() { return suggestedOfficialPrice; }
    public void setSuggestedOfficialPrice(BigDecimal suggestedOfficialPrice) { this.suggestedOfficialPrice = suggestedOfficialPrice; }
}
