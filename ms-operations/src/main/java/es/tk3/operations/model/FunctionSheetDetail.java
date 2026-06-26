package es.tk3.operations.model;

import es.tk3.operations.enums.RoleDish;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "function_sheet_details")
public class FunctionSheetDetail {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "function_sheet_id", nullable = false)
    private FunctionSheet functionSheet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish;

    @Column(name = "custom_name", nullable = false)
    private String customName;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private RoleDish itemType;

    @Column(name = "final_price", nullable = false)
    private BigDecimal finalPrice;

    @Column(name = "unit_cost_at_event", nullable = false)
    private BigDecimal unitCostAtEvent;

    @Column(nullable = false)
    private Integer quantity;

    public FunctionSheetDetail() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public FunctionSheet getFunctionSheet() { return functionSheet; }
    public void setFunctionSheet(FunctionSheet functionSheet) { this.functionSheet = functionSheet; }

    public Dish getDish() { return dish; }
    public void setDish(Dish dish) { this.dish = dish; }

    public String getCustomName() { return customName; }
    public void setCustomName(String customName) { this.customName = customName; }

    public BigDecimal getFinalPrice() { return finalPrice; }
    public void setFinalPrice(BigDecimal finalPrice) { this.finalPrice = finalPrice; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public RoleDish getItemType() { return itemType; }
    public void setItemType(RoleDish itemType) { this.itemType = itemType; }

    public BigDecimal getUnitCostAtEvent() { return unitCostAtEvent; }
    public void setUnitCostAtEvent(BigDecimal unitCostAtEvent) { this.unitCostAtEvent = unitCostAtEvent; }
}