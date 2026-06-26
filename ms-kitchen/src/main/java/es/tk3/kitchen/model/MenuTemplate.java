package es.tk3.kitchen.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menu_templates")
public class MenuTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_template_id")
    @OrderBy("sequence_order ASC")
    private List<MenuCategory> categories;

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice = BigDecimal.ZERO;

    public MenuTemplate() {
        this.categories = new ArrayList<>();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<MenuCategory> getCategories() { return categories; }
    public void setCategories(List<MenuCategory> categories) { this.categories = categories; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
}
