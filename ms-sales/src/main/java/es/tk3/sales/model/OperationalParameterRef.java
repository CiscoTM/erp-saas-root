package es.tk3.sales.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "operational_parameter_ref")
public class OperationalParameterRef {

    @Id
    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "overhead_percentage")
    private BigDecimal overheadPercentage;
    @Column(name = "risk_margin_threshold")
    private BigDecimal riskMarginThreshold;
    @Column(name = "optimal_margin_threshold")
    private BigDecimal optimalMarginThreshold;

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
}
