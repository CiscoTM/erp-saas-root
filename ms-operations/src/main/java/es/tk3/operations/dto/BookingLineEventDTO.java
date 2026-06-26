package es.tk3.operations.dto;

import java.math.BigDecimal;
import java.util.Map;

public class BookingLineEventDTO {
    private String commercialMenuId;
    private Integer paxCount;
    private Double agreedPricePerPax;
    private Map<String, Integer> dishSelections;
    private BigDecimal snapshotRiskMargin;
    private BigDecimal snapshotOptimalMargin;

    public BookingLineEventDTO() {}

    public String getCommercialMenuId() {
        return commercialMenuId;
    }

    public void setCommercialMenuId(String commercialMenuId) {
        this.commercialMenuId = commercialMenuId;
    }

    public Integer getPaxCount() {
        return paxCount;
    }

    public void setPaxCount(Integer paxCount) {
        this.paxCount = paxCount;
    }

    public Double getAgreedPricePerPax() {
        return agreedPricePerPax;
    }

    public void setAgreedPricePerPax(Double agreedPricePerPax) {
        this.agreedPricePerPax = agreedPricePerPax;
    }

    public Map<String, Integer> getDishSelections() {
        return dishSelections;
    }

    public void setDishSelections(Map<String, Integer> dishSelections) {
        this.dishSelections = dishSelections;
    }

    public BigDecimal getSnapshotRiskMargin() {
        return snapshotRiskMargin;
    }

    public void setSnapshotRiskMargin(BigDecimal snapshotRiskMargin) {
        this.snapshotRiskMargin = snapshotRiskMargin;
    }

    public BigDecimal getSnapshotOptimalMargin() {
        return snapshotOptimalMargin;
    }

    public void setSnapshotOptimalMargin(BigDecimal snapshotOptimalMargin) {
        this.snapshotOptimalMargin = snapshotOptimalMargin;
    }
}
