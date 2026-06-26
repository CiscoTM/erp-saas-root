package es.tk3.kitchen.model;

import es.tk3.kitchen.enums.ServiceType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "dishes")
public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "technical_name", nullable = false)
    private String technicalName;

    @OneToMany(mappedBy = "dish", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DishPreparation> preparations = new ArrayList<>();

    @Column(name = "base_cost")
    private BigDecimal baseCost;

    @Column(name = "diners_per_plate")
    private Integer dinersPerPlate;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type")
    private ServiceType serviceType;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "dish_allergens",
            joinColumns = @JoinColumn(name = "dish_id"),
            inverseJoinColumns = @JoinColumn(name = "allergen_id")
    )
    private Set<Allergen> allergens = new HashSet<>();

    public Dish() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTechnicalName() { return technicalName; }
    public void setTechnicalName(String technicalName) { this.technicalName = technicalName; }

    public List<DishPreparation> getPreparations() {
        return preparations != null ? preparations : new ArrayList<>();
    }

    public void setPreparations(List<DishPreparation> preparations) {
        this.preparations = preparations != null ? preparations : new ArrayList<>();
    }

    public BigDecimal getBaseCost() {return baseCost; }
    public void setBaseCost(BigDecimal baseCost) { this.baseCost = baseCost; }
    public Integer getDinersPerPlate() { return dinersPerPlate; }
    public void setDinersPerPlate(Integer dinersPerPlate) { this.dinersPerPlate = dinersPerPlate; }
    public ServiceType getServiceType() { return serviceType; }
    public void setServiceType(ServiceType serviceType) { this.serviceType = serviceType; }

    public Set<Allergen> getAllergens() {
        if (preparations == null) return new HashSet<>();

        Set<Allergen> calculatedAllergens = new HashSet<>();

        for (DishPreparation p : preparations) {
            if (p.getRawMaterial() != null && p.getRawMaterial().getAllergens() != null) {
                for (RawMaterialAllergen rma : p.getRawMaterial().getAllergens()) {
                    calculatedAllergens.add(rma.getAllergen());
                }
            }
            if (p.getRecipe() != null && p.getRecipe().getAllergens() != null) {
                calculatedAllergens.addAll(p.getRecipe().getAllergens());
            }
        }
        if (this.allergens != null) {
            calculatedAllergens.addAll(this.allergens);
        }

        return calculatedAllergens;
    }

    public void setAllergens(Set<Allergen> allergens) {
        this.allergens = allergens;
    }
}