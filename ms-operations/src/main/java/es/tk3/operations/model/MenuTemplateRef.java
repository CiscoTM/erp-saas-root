package es.tk3.operations.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "menu_template_ref")
public class MenuTemplateRef {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category_structure_json", columnDefinition = "TEXT")
    private String categoryStructureJson;

    public MenuTemplateRef() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategoryStructureJson() { return categoryStructureJson; }
    public void setCategoryStructureJson(String categoryStructureJson) { this.categoryStructureJson = categoryStructureJson; }
}