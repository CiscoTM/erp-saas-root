package es.tk3.kitchen.dto.event;

import java.math.BigDecimal;

public record RawMaterialPriceUpdatedEvent(
        String tenantId,
        Long rawMaterialId,
        BigDecimal newPrice
) {}
