package es.tk3.operations.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class BookingEventDTO {
    private UUID eventId; // El ID del Outbox para la idempotencia
    private Long bookingId;
    private String tenantId;
    private String eventType;
    private String eventName;
    private LocalDateTime startDate;
    private Integer guests;
    private String roomName;

    public BookingEventDTO() {}

    // Getters y Setters
    public UUID getEventId() { return eventId; }
    public void setEventId(UUID eventId) { this.eventId = eventId; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public Integer getGuests() { return guests; }
    public void setGuests(Integer guests) { this.guests = guests; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
}