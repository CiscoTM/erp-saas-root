package es.tk3.kitchen.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "menu_category_optional_dishes")
public class MenuCategoryOptionalDish {

    @EmbeddedId
    private MenuCategoryOptionalDishId id = new MenuCategoryOptionalDishId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("menuCategoryId")
    @JoinColumn(name = "menu_category_id")
    private MenuCategory menuCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("dishId")
    @JoinColumn(name = "dish_id")
    private Dish dish;

    @Column(name = "extra_price", nullable = false)
    private BigDecimal extraPrice = BigDecimal.ZERO;

    public MenuCategoryOptionalDish() {}

    public MenuCategoryOptionalDishId getId() {
        return id;
    }

    public void setId(MenuCategoryOptionalDishId id) {
        this.id = id;
    }

    public MenuCategory getMenuCategory() {
        return menuCategory;
    }

    public void setMenuCategory(MenuCategory menuCategory) {
        this.menuCategory = menuCategory;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public BigDecimal getExtraPrice() {
        return extraPrice;
    }

    public void setExtraPrice(BigDecimal extraPrice) {
        this.extraPrice = extraPrice;
    }
}