package es.tk3.sales.dto;

import jakarta.persistence.Column;

import java.math.BigDecimal;
import java.util.Map;

public class BookingLineDTO {
    private String commercialMenuId;
    private Integer paxCount;
    private Double agreedPricePerPax;
    private Map<String, Integer> dishSelections;
    private BigDecimal snapshotOverheadPercentage;
    private BigDecimal snapshotRiskMarginThreshold;
    private BigDecimal snapshotOptimalMarginThreshold;


    public String getCommercialMenuId() { return commercialMenuId; }
    public void setCommercialMenuId(String commercialMenuId) { this.commercialMenuId = commercialMenuId; }
    public Integer getPaxCount() { return paxCount; }
    public void setPaxCount(Integer paxCount) { this.paxCount = paxCount; }
    public Double getAgreedPricePerPax() { return agreedPricePerPax; }
    public void setAgreedPricePerPax(Double agreedPricePerPax) { this.agreedPricePerPax = agreedPricePerPax; }
    public Map<String, Integer> getDishSelections() { return dishSelections; }
    public void setDishSelections(Map<String, Integer> dishSelections) { this.dishSelections = dishSelections; }
    public BigDecimal getSnapshotOverheadPercentage() { return snapshotOverheadPercentage; }
    public void setSnapshotOverheadPercentage(BigDecimal snapshotOverheadPercentage) { this.snapshotOverheadPercentage = snapshotOverheadPercentage; }
    public BigDecimal getSnapshotRiskMarginThreshold() { return snapshotRiskMarginThreshold; }
    public void setSnapshotRiskMarginThreshold(BigDecimal snapshotRiskMarginThreshold) { this.snapshotRiskMarginThreshold = snapshotRiskMarginThreshold; }
    public BigDecimal getSnapshotOptimalMarginThreshold() { return snapshotOptimalMarginThreshold; }
    public void setSnapshotOptimalMarginThreshold(BigDecimal snapshotOptimalMarginThreshold) { this.snapshotOptimalMarginThreshold = snapshotOptimalMarginThreshold; }
}