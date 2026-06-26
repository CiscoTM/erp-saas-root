package es.tk3.sales.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FinancialAlertEventDTO {
    private String tenantId;
    private Long bookingId;
    private String eventName;
    private String commercialMenuId;
    private Integer paxCount;
    private BigDecimal agreedPricePerPax;
    private BigDecimal minimumRequiredPriceFloor;
    private String salesRepUsername; // Quién forzó el bypass
    private LocalDateTime alertTimestamp;

    public FinancialAlertEventDTO() {}

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

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

    public BigDecimal getAgreedPricePerPax() {
        return agreedPricePerPax;
    }

    public void setAgreedPricePerPax(BigDecimal agreedPricePerPax) {
        this.agreedPricePerPax = agreedPricePerPax;
    }

    public BigDecimal getMinimumRequiredPriceFloor() {
        return minimumRequiredPriceFloor;
    }

    public void setMinimumRequiredPriceFloor(BigDecimal minimumRequiredPriceFloor) {
        this.minimumRequiredPriceFloor = minimumRequiredPriceFloor;
    }

    public String getSalesRepUsername() {
        return salesRepUsername;
    }

    public void setSalesRepUsername(String salesRepUsername) {
        this.salesRepUsername = salesRepUsername;
    }

    public LocalDateTime getAlertTimestamp() {
        return alertTimestamp;
    }

    public void setAlertTimestamp(LocalDateTime alertTimestamp) {
        this.alertTimestamp = alertTimestamp;
    }
}
