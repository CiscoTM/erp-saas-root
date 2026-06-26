package es.tk3.operations.dto;

import java.math.BigDecimal;

public class OperationalParamRequestDTO {
    private String tenantId;
    private BigDecimal overheadPercentage;
    private BigDecimal riskMarginThreshold;
    private BigDecimal optimalMarginThreshold;

    private BigDecimal minimumProfitMargin;
    private BigDecimal optimalProfitMargin;

    public OperationalParamRequestDTO() {}

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public BigDecimal getOverheadPercentage() {
        return overheadPercentage;
    }

    public void setOverheadPercentage(BigDecimal overheadPercentage) {
        this.overheadPercentage = overheadPercentage;
    }

    public BigDecimal getRiskMarginThreshold() {
        return riskMarginThreshold;
    }

    public void setRiskMarginThreshold(BigDecimal riskMarginThreshold) {
        this.riskMarginThreshold = riskMarginThreshold;
    }

    public BigDecimal getOptimalMarginThreshold() {
        return optimalMarginThreshold;
    }

    public void setOptimalMarginThreshold(BigDecimal optimalMarginThreshold) {
        this.optimalMarginThreshold = optimalMarginThreshold;
    }

    public BigDecimal getMinimumProfitMargin() {
        return minimumProfitMargin;
    }

    public void setMinimumProfitMargin(BigDecimal minimumProfitMargin) {
        this.minimumProfitMargin = minimumProfitMargin;
    }

    public BigDecimal getOptimalProfitMargin() {
        return optimalProfitMargin;
    }

    public void setOptimalProfitMargin(BigDecimal optimalProfitMargin) {
        this.optimalProfitMargin = optimalProfitMargin;
    }
}
