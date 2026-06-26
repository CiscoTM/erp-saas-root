package es.tk3.kitchen.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "recipes")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String category;

    @Column(name = "preparation_steps", columnDefinition = "TEXT")
    private String preparationSteps;

    @Column(name = "base_cost", precision = 10, scale = 2)
    private BigDecimal baseCost;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "recipe_allergens",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "allergen_id")
    )
    private Set<Allergen> allergens = new HashSet<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    @Column(name = "total_yield")
    private Double totalYield;

    @Column(name = "yield_unit")
    private String yieldUnit;

    public Recipe() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getPreparationSteps() { return preparationSteps; }
    public void setPreparationSteps(String steps) { this.preparationSteps = steps; }

    public Set<Allergen> getAllergens() { return allergens; }
    public void setAllergens(Set<Allergen> allergens) { this.allergens = allergens; }

    public BigDecimal getBaseCost() { return baseCost; }
    public void setBaseCost(BigDecimal cost) { this.baseCost = cost; }
    public List<RecipeIngredient> getIngredients() { return ingredients; }
    public void setIngredients(List<RecipeIngredient> ingredients) { this.ingredients = ingredients; }
    public Double getTotalYield() { return totalYield; }
    public void setTotalYield(Double totalYield) { this.totalYield = totalYield; }
    public String getYieldUnit() { return yieldUnit; }
    public void setYieldUnit(String yieldUnit) { this.yieldUnit = yieldUnit; }
}