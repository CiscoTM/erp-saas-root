package es.tk3.sales.dto;

import java.util.Map;

public class BookingLineEventDTO {
    private String commercialMenuId;
    private Integer paxCount;
    private Double agreedPricePerPax;
    private Map<String, Integer> dishSelections;

    public String getCommercialMenuId() { return commercialMenuId; }
    public void setCommercialMenuId(String commercialMenuId) { this.commercialMenuId = commercialMenuId; }
    public Integer getPaxCount() { return paxCount; }
    public void setPaxCount(Integer paxCount) { this.paxCount = paxCount; }
    public Double getAgreedPricePerPax() { return agreedPricePerPax; }
    public void setAgreedPricePerPax(Double agreedPricePerPax) { this.agreedPricePerPax = agreedPricePerPax; }
    public Map<String, Integer> getDishSelections() { return dishSelections; }
    public void setDishSelections(Map<String, Integer> dishSelections) { this.dishSelections = dishSelections; }
}
