package es.tk3.kitchen.service;

import es.tk3.common.outbox.service.OutboxEventService;
import es.tk3.kitchen.dto.dish.DishResponseDTO;
import es.tk3.kitchen.dto.menu.*;
import es.tk3.kitchen.model.Dish;
import es.tk3.kitchen.model.MenuCategory;
import es.tk3.kitchen.model.MenuCategoryOptionalDish;
import es.tk3.kitchen.model.MenuTemplate;
import es.tk3.kitchen.repository.DishRepository;
import es.tk3.kitchen.repository.MenuTemplateRepository;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenuTemplateService {

    private final MenuTemplateRepository menuTemplateRepository;
    private final DishRepository dishRepository;
    private final OutboxEventService outboxEventService;

    public MenuTemplateService(
            MenuTemplateRepository menuTemplateRepository,
            DishRepository dishRepository,
            OutboxEventService outboxEventService) {
        this.menuTemplateRepository = menuTemplateRepository;
        this.dishRepository = dishRepository;
        this.outboxEventService = outboxEventService;
    }

    public List<MenuTemplateResponseDTO> getAllTemplates() {
        return menuTemplateRepository.findAll()
                .stream()
                .map(template -> {
                    List<CategoryResponseDTO> categoryDTOs = template.getCategories().stream().map(cat -> {

                        List<MenuCatalogDishDTO> optional = cat.getOptionalDishes().stream()
                                .map(opt -> new MenuCatalogDishDTO(
                                        opt.getDish().getId(),
                                        opt.getDish().getTechnicalName(),
                                        opt.getExtraPrice()
                                )).collect(Collectors.toList());

                        return new CategoryResponseDTO(
                                cat.getCategoryName(),
                                cat.getSelectableCount(),
                                optional
                        );

                    }).collect(Collectors.toList());

                    return new MenuTemplateResponseDTO(
                            template.getId(),
                            template.getName(),
                            template.getBasePrice(),
                            categoryDTOs
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void createAndSyncTemplate(MenuTemplateRequestDTO dto){
        MenuTemplate template = new MenuTemplate();
        template.setName(dto.getName());

        template.setBasePrice(dto.getBasePrice() != null ? dto.getBasePrice() : BigDecimal.ZERO);
        template.setCategories(new ArrayList<>());

        for (MenuCategoryRequestDTO catDto : dto.getCategories()){
            MenuCategory category = new MenuCategory();
            category.setCategoryName(catDto.getName());
            category.setFixedDishes(new HashSet<>());
            category.setOptionalDishes(new HashSet<>());
            category.setSequenceOrder(catDto.getSequenceOrder());
            category.setSelectableCount(catDto.getSelectableCount());
            category.setMenuTemplate(template);

            if(catDto.getFixedDishIds() != null){
                for(Long dishId : catDto.getFixedDishIds()){
                    Dish dish = dishRepository.findById(dishId)
                            .orElseThrow(() -> new ResourceNotFoundException("Plato fijo no encontrado"));
                    category.getFixedDishes().add(dish);
                }
            }

            if(catDto.getOptionalDishIds() != null){
                for(Long dishId : catDto.getOptionalDishIds()){
                    Dish dish = dishRepository.findById(dishId)
                            .orElseThrow(() -> new ResourceNotFoundException("Plato opcional no encontrado"));
                    MenuCategoryOptionalDish optional = new MenuCategoryOptionalDish();
                    optional.setMenuCategory(category);
                    optional.setDish(dish);
                    optional.setExtraPrice(BigDecimal.ZERO);

                    category.getOptionalDishes().add(optional);
                }
            }
            template.getCategories().add(category);
        }

        MenuTemplate savedTemplate = menuTemplateRepository.save(template);

        outboxEventService.createAndSaveEvent(
                savedTemplate.getId().toString(),
                "MenuTemplate",
                "kitchen.menu-templates.sync",
                "MENU_TEMPLATE_CREATED",
                MenuTemplateSyncEventDTO.fromEntity(savedTemplate)
                );
    }
}
