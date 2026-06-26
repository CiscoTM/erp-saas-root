package es.tk3.operations.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MenuTemplateSyncEventDTO {

    private String tenantId;
    private Long templateId;
    private String name;
    private List<CategoryPayload> categories;

    public MenuTemplateSyncEventDTO() {
    }

    public static class CategoryPayload {
        private String name;
        private List<Long> fixedDishIds;
        private List<Long> optionalDishIds;

        public CategoryPayload() {
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public List<Long> getFixedDishIds() { return fixedDishIds; }
        public void setFixedDishIds(List<Long> fixedDishIds) { this.fixedDishIds = fixedDishIds; }

        public List<Long> getOptionalDishIds() { return optionalDishIds; }
        public void setOptionalDishIds(List<Long> optionalDishIds) { this.optionalDishIds = optionalDishIds; }
    }

    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<CategoryPayload> getCategories() { return categories; }
    public void setCategories(List<CategoryPayload> categories) { this.categories = categories; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
}