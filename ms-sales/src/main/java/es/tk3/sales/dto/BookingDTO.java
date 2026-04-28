package es.tk3.sales.dto;

import java.time.LocalDateTime;

public class BookingDTO {
    private Long id;
    private String eventName;
    private String customerName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private Double totalPrice;
    private Integer expectedGuests;
    private String roomName;

    public BookingDTO() {}

    // Getters
    public Long getId() { return id; }
    public String getEventName() { return eventName; }
    public String getCustomerName() { return customerName; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public String getStatus() { return status; }
    public Double getTotalPrice() { return totalPrice; }
    public Integer getExpectedGuests() { return expectedGuests; }
    public String getRoomName() { return roomName; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public void setStatus(String status) { this.status = status; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    public void setExpectedGuests(Integer expectedGuests) { this.expectedGuests = expectedGuests; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
}