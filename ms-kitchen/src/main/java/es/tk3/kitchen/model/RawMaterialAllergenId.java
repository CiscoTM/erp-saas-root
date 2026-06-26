package es.tk3.kitchen.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RawMaterialAllergenId implements Serializable {
    private Long rawMaterialId;
    private Long allergenId;

    public RawMaterialAllergenId() {}

    public RawMaterialAllergenId(Long rawMaterialId, Long allergenId) {
        this.rawMaterialId = rawMaterialId;
        this.allergenId = allergenId;
    }

    public Long getRawMaterialId() { return rawMaterialId; }
    public void setRawMaterialId(Long rawMaterialId) { this.rawMaterialId = rawMaterialId; }

    public Long getAllergenId() { return allergenId; }
    public void setAllergenId(Long allergenId) { this.allergenId = allergenId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawMaterialAllergenId integrity = (RawMaterialAllergenId) o;
        return Objects.equals(rawMaterialId, integrity.rawMaterialId) && Objects.equals(allergenId, integrity.allergenId);
    }

    @Override
    public int hashCode() { return Objects.hash(rawMaterialId, allergenId); }
}