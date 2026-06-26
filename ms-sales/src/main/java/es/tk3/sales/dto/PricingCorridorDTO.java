package es.tk3.sales.dto;

import java.math.BigDecimal;

public record PricingCorridorDTO(
        BigDecimal minimumPerPax,
        BigDecimal recommendedPrice,
        BigDecimal suggestedPolicyPrice,
        String policyApplied,
        BigDecimal appliedOverhead,
        BigDecimal appliedRiskMargin,
        BigDecimal appliedOptimalMargin
) {}


