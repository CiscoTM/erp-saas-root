package es.tk3.events;

import java.time.LocalDateTime;


public class BookingConfirmedEvent {
    private Long bookingId;
    private String tenantId;
    private String eventName;
    private LocalDateTime startDate;
    private Integer guests;
    private String roomName;

    public BookingConfirmedEvent(Long bookingId, String tenantId, String eventName, LocalDateTime startDate, Integer guests, String roomName) {
        this.bookingId = bookingId;
        this.tenantId = tenantId;
        this.eventName = eventName;
        this.startDate = startDate;
        this.guests = guests;
        this.roomName = roomName;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getEventName() {
        return eventName;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public Integer getGuests() {
        return guests;
    }

    public String getRoomName() {
        return roomName;
    }
}
