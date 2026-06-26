package es.tk3.operations.model;

import jakarta.persistence.*;

@Entity
@Table(name = "commercial_menu_recipes")
public class CommercialMenuRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "commercial_menu_code", nullable = false)
    private String commercialMenuId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish;

    @Column(name = "default_quantity", nullable = false)
    private Integer defaultQuantity = 1;

    public CommercialMenuRecipe() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCommercialMenuId() {
        return commercialMenuId;
    }

    public void setCommercialMenuId(String commercialMenuId) {
        this.commercialMenuId = commercialMenuId;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public Integer getDefaultQuantity() {
        return defaultQuantity;
    }

    public void setDefaultQuantity(Integer defaultQuantity) {
        this.defaultQuantity = defaultQuantity;
    }
}
