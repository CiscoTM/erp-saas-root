package es.tk3.operations.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "operational_parameters")
public class OperationalParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", length = 50, nullable = false, unique = true)
    private String tenantId;

    @Column(name = "overhead_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal overheadPercentage;

    @Column(name = "risk_margin_threshold", nullable = false, precision = 5, scale = 2)
    private BigDecimal riskMarginThreshold;

    @Column(name = "optimal_margin_threshold", nullable = false, precision = 5, scale = 2)
    private BigDecimal optimalMarginThreshold;

    @Column(name = "minimum_profit_margin", nullable = false, precision = 5, scale = 2)
    private BigDecimal minimumProfitMargin = new BigDecimal("0.20");

    @Column(name = "optimal_profit_margin", nullable = false, precision = 5, scale = 2)
    private BigDecimal optimalProfitMargin = new BigDecimal("0.75");

    public OperationalParameter() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
