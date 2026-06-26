package es.tk3.kitchen.dto.menu;

import es.tk3.common.tenant.TenantContext;
import es.tk3.kitchen.model.MenuCategory;
import es.tk3.kitchen.model.MenuTemplate;
import es.tk3.kitchen.model.Dish;
import es.tk3.kitchen.model.MenuCategoryOptionalDish; // <-- NUEVO IMPORT

import java.util.ArrayList;
import java.util.List;

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Long> getFixedDishIds() {
            return fixedDishIds;
        }

        public void setFixedDishIds(List<Long> fixedDishIds) {
            this.fixedDishIds = fixedDishIds;
        }

        public List<Long> getOptionalDishIds() {
            return optionalDishIds;
        }

        public void setOptionalDishIds(List<Long> optionalDishIds) {
            this.optionalDishIds = optionalDishIds;
        }
    }

    public static MenuTemplateSyncEventDTO fromEntity(MenuTemplate entity) {
        MenuTemplateSyncEventDTO dto = new MenuTemplateSyncEventDTO();
        dto.setTemplateId(entity.getId());
        dto.setName(entity.getName());
        dto.setTenantId(TenantContext.getTenantId());

        List<CategoryPayload> categoryPayloads = new ArrayList<>();
        if (entity.getCategories() != null) {
            for (MenuCategory cat : entity.getCategories()) {
                CategoryPayload cp = new CategoryPayload();
                cp.setName(cat.getCategoryName());

                List<Long> fixedIds = new ArrayList<>();
                if (cat.getFixedDishes() != null) {
                    for (Dish d : cat.getFixedDishes()) {
                        fixedIds.add(d.getId());
                    }
                }
                cp.setFixedDishIds(fixedIds);

                // CORREGIDO: Iteramos sobre MenuCategoryOptionalDish en lugar de Dish directo
                List<Long> optionalIds = new ArrayList<>();
                if (cat.getOptionalDishes() != null) {
                    for (MenuCategoryOptionalDish mcod : cat.getOptionalDishes()) {
                        if (mcod.getDish() != null) {
                            optionalIds.add(mcod.getDish().getId());
                        }
                    }
                }
                cp.setOptionalDishIds(optionalIds);

                categoryPayloads.add(cp);
            }
        }
        dto.setCategories(categoryPayloads);
        return dto;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CategoryPayload> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryPayload> categories) {
        this.categories = categories;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}