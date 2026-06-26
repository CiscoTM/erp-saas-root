package es.tk3.kitchen.dto.menu;

import java.math.BigDecimal;
import java.util.List;

public record MenuTemplateResponseDTO(
        Long id,
        String name,
        BigDecimal basePrice,
        List<CategoryResponseDTO> categories
) {}

