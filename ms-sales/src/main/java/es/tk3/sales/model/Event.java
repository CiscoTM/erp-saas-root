package es.tk3.sales.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Event title is required")
    private String title;

    @ManyToOne
    @JoinColumn(name = "event_type_id")
    @NotNull(message = "Event type is required")
    private EventType eventType;

    @NotBlank(message = "The lounge/space is required")
    private String roomName;

    @NotNull(message = "Start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;

    private String status = "PRESUPUESTO";
    @Column(name = "contact_name")
    private String contactName;
    @Column(name = "contact_phone")
    private String contactPhone;
    @Column(name = "expected_guests")
    private Integer expectedGuests = 0;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getRoomName() {
        return roomName;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public Integer getExpectedGuests() {
        return expectedGuests;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public void setExpectedGuests(Integer expectedGuests) {
        this.expectedGuests = expectedGuests;
    }
}
