package es.tk3.operations.model;

import es.tk3.operations.enums.Profitability;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "function_sheets")
public class FunctionSheet {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "room_name")
    private String roomName;

    @Column(name = "guests_count")
    private Integer guestsCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commercial_menu_id")
    private CommercialMenu commercialMenu;

    @OneToMany(mappedBy = "functionSheet", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<FunctionSheetDetail> details;

    @Column(name = "agreed_menu_price")
    private BigDecimal agreedMenuPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "profitability_status")
    private Profitability profitabilityStatus;

    @Column(nullable = false)
    private String status;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public FunctionSheet() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = "DRAFT";
    }

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public Integer getGuestsCount() { return guestsCount; }
    public void setGuestsCount(Integer guestsCount) { this.guestsCount = guestsCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public CommercialMenu getCommercialMenu() { return commercialMenu; }
    public void setCommercialMenu(CommercialMenu commercialMenu) { this.commercialMenu = commercialMenu; }

    public BigDecimal getAgreedMenuPrice() { return agreedMenuPrice; }
    public void setAgreedMenuPrice(BigDecimal agreedMenuPrice) { this.agreedMenuPrice = agreedMenuPrice; }

    public Profitability getProfitabilityStatus() { return profitabilityStatus; }
    public void setProfitabilityStatus(Profitability profitabilityStatus) { this.profitabilityStatus = profitabilityStatus; }

    public List<FunctionSheetDetail> getDetails() { return details; }
    public void setDetails(List<FunctionSheetDetail> details) { this.details = details; }
}
