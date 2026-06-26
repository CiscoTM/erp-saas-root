package es.tk3.sales.dto;

import es.tk3.sales.model.Customer;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class BookingDTO {
    private Long id;
    private String eventName;
    @NotNull(message = "El tipo de evento es obligatorio")
    private Integer eventTypeId;
    private String eventTypeName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long roomId;
    private Integer expectedGuests;
    private List<BookingLineDTO> lines;
    private CustomerDTO customer;
    private Boolean forceOverride = false;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public Integer getExpectedGuests() { return expectedGuests; }
    public void setExpectedGuests(Integer expectedGuests) { this.expectedGuests = expectedGuests; }
    public List<BookingLineDTO> getLines() { return lines; }
    public void setLines(List<BookingLineDTO> lines) { this.lines = lines; }
    public Integer getEventTypeId() { return eventTypeId; }
    public void setEventTypeId(Integer eventTypeId) { this.eventTypeId = eventTypeId; }
    public String getEventTypeName() { return eventTypeName; }
    public void setEventTypeName(String eventTypeName) { this.eventTypeName = eventTypeName; }
    public CustomerDTO getCustomer() { return customer; }
    public void setCustomer(CustomerDTO customer) { this.customer = customer; }
    public Boolean getForceOverride() { return forceOverride; }
    public void setForceOverride(Boolean forceOverride) { this.forceOverride = forceOverride; }
}