package es.tk3.kitchen.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import es.tk3.kitchen.enums.PresenceType;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "raw_material_allergens")
public class RawMaterialAllergen {

    @EmbeddedId
    private RawMaterialAllergenId id = new RawMaterialAllergenId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("rawMaterialId")
    @JoinColumn(name = "raw_material_id")
    @JsonBackReference
    private RawMaterial rawMaterial;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("allergenId")
    @JoinColumn(name = "allergen_id")
    private Allergen allergen;

    @Enumerated(EnumType.STRING)
    @Column(name = "presence_type", nullable = false)
    private PresenceType presenceType = PresenceType.CONTAINS;

    public RawMaterialAllergen() {}

    public RawMaterialAllergen(RawMaterial rawMaterial, Allergen allergen, PresenceType presenceType) {
        this.rawMaterial = rawMaterial;
        this.allergen = allergen;
        this.presenceType = presenceType;
        this.id = new RawMaterialAllergenId(rawMaterial.getId(), allergen.getId());
    }

    public RawMaterialAllergenId getId() { return id; }
    public void setId(RawMaterialAllergenId id) { this.id = id; }

    public RawMaterial getRawMaterial() { return rawMaterial; }
    public void setRawMaterial(RawMaterial rawMaterial) { this.rawMaterial = rawMaterial; }

    public Allergen getAllergen() { return allergen; }
    public void setAllergen(Allergen allergen) { this.allergen = allergen; }

    public PresenceType getPresenceType() { return presenceType; }
    public void setPresenceType(PresenceType presenceType) { this.presenceType = presenceType; }

    @Override
    public boolean equals( Object someObj ) {
        if (this == someObj) return true;
        if (someObj == null || getClass() != someObj.getClass()) return false;
        RawMaterialAllergen that = (RawMaterialAllergen) someObj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}