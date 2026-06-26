package es.tk3.kitchen.model;

import es.tk3.kitchen.enums.RestrictionType;
import jakarta.persistence.*;

@Entity
@Table(name = "allergens")
public class Allergen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "restriction_type", nullable = false)
    private RestrictionType restrictionType = RestrictionType.OFFICIAL_ALLERGEN; // Valor por defecto

    public Allergen() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public Boolean getActive() { return isActive; }
    public void setActive(Boolean active) { isActive = active; }

    public RestrictionType getRestrictionType() { return restrictionType; }
    public void setRestrictionType(RestrictionType restrictionType) { this.restrictionType = restrictionType; }
}