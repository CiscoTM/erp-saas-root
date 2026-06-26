package es.tk3.kitchen.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MenuCategoryOptionalDishId implements Serializable {
    private Long menuCategoryId;
    private Long dishId;

    public MenuCategoryOptionalDishId() {}
    public MenuCategoryOptionalDishId(Long menuCategoryId, Long dishId) {
        this.menuCategoryId = menuCategoryId;
        this.dishId = dishId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MenuCategoryOptionalDishId that = (MenuCategoryOptionalDishId) o;
        return Objects.equals(menuCategoryId, that.menuCategoryId) && Objects.equals(dishId, that.dishId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menuCategoryId, dishId);
    }

    public Long getMenuCategoryId() {
        return menuCategoryId;
    }

    public void setMenuCategoryId(Long menuCategoryId) {
        this.menuCategoryId = menuCategoryId;
    }

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }
}
