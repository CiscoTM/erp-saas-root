package es.tk3.sales.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class BookingEventDTO {
    private Long id;
    private String eventName;
    private String eventType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double totalPrice;
    private String status;
    private String tenantId;
    private List<BookingLineEventDTO> lines;
    private String roomName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<BookingLineEventDTO> getLines() { return lines; }
    public void setLines(List<BookingLineEventDTO> lines) { this.lines = lines; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
}
