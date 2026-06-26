package es.tk3.kitchen.service;

import java.math.BigDecimal;

public interface CostCalculatorProxy {
    BigDecimal getRawMaterialPrice(Long rawMaterialId);
}
