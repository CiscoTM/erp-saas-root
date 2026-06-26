package es.tk3.kitchen.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "menu_categories")
public class MenuCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String categoryName;

    @Column(name = "sequence_order")
    private Integer sequenceOrder;

    @Column(name = "selectable_count")
    private Integer selectableCount;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "menu_category_fixed_dishes",
            joinColumns = @JoinColumn(name = "menu_category_id"),
            inverseJoinColumns = @JoinColumn(name = "dish_id")
    )
    private Set<Dish> fixedDishes;

    @OneToMany(mappedBy = "menuCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MenuCategoryOptionalDish> optionalDishes = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_template_id", nullable = false)
    private MenuTemplate menuTemplate;

    public MenuCategory() {
        this.fixedDishes = new HashSet<>();
        this.optionalDishes = new HashSet<>();
    }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder(Integer sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }

    public Integer getSelectableCount() {
        return selectableCount;
    }

    public void setSelectableCount(Integer selectableCount) {
        this.selectableCount = selectableCount;
    }

    public Set<Dish> getFixedDishes() {
        return fixedDishes;
    }

    public void setFixedDishes(Set<Dish> fixedDishes) {
        this.fixedDishes = fixedDishes;
    }

    public Set<MenuCategoryOptionalDish> getOptionalDishes() {
        return optionalDishes;
    }

    public void setOptionalDishes(Set<MenuCategoryOptionalDish> optionalDishes) {
        this.optionalDishes = optionalDishes;
    }

    public MenuTemplate getMenuTemplate() {
        return menuTemplate;
    }

    public void setMenuTemplate(MenuTemplate menuTemplate) {
        this.menuTemplate = menuTemplate;
    }
}