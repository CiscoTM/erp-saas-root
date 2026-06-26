package es.tk3.kitchen.dto.menu;


import java.util.List;

public record CategoryResponseDTO(
        String name,
        Integer selectableCount,
        List<MenuCatalogDishDTO> optionalDishes
) {}
