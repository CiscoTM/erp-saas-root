package es.tk3.kitchen.dto.menu;

import java.math.BigDecimal;

public record MenuCatalogDishDTO(
        Long dishId,
        String dishName,
        BigDecimal extraPrice
) {}