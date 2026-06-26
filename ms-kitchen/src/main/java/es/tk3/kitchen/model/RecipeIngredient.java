package es.tk3.kitchen.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "recipe_ingredients")
public class RecipeIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id",  nullable = false)
    @JsonBackReference
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_material_id")
    private RawMaterial rawMaterial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_recipe_id")
    private Recipe subRecipe;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal quantity;

    @Column(nullable = false)
    private String unit;

    public RecipeIngredient() {}

    public String getDisplayName() {
        if (rawMaterial != null) return rawMaterial.getName();
        if (subRecipe != null) return subRecipe.getName();
        return "Desconocido";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Recipe getRecipe() { return recipe; }
    public void setRecipe(Recipe recipe) { this.recipe = recipe; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public RawMaterial getRawMaterial() { return rawMaterial; }
    public void setRawMaterial(RawMaterial rawMaterial) { this.rawMaterial = rawMaterial; }
    public Recipe getSubRecipe() { return subRecipe; }
    public void setSubRecipe(Recipe subRecipe) { this.subRecipe = subRecipe; }
}