package es.tk3.sales.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.Duration;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingLine> lines = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_type_id")
    private EventType eventType;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "expected_guests")
    private Integer expectedGuests;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "is_deficit_margin")
    private Boolean isDeficitMargin = false;
    @Column(name = "force_override")
    private Boolean forceOverride;

    public void calculatePrice() {
        double roomTotal = 0.0;
        if (room != null && startDate != null && endDate != null) {
            long hours = Duration.between(startDate, endDate).toHours();
            if (hours == 0) hours = 1;
            roomTotal = hours * room.getPricePerHour();
        }

        double linesTotal = lines.stream()
                .mapToDouble(line -> line.getAgreedPricePerPax() != null && line.getPaxCount() != null ?
                        line.getAgreedPricePerPax() * line.getPaxCount() : 0.0)
                .sum();

        this.totalPrice = roomTotal + linesTotal;
    }

    public void addLine(BookingLine bookingLine) {
        lines.add(bookingLine);
        bookingLine.setBooking(this);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    public Integer getExpectedGuests() { return expectedGuests; }
    public void setExpectedGuests(Integer expectedGuests) { this.expectedGuests = expectedGuests; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public List<BookingLine> getLines() { return lines; }
    public void setLines(List<BookingLine> lines) { this.lines = lines; }
    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Boolean getIsDeficitMargin() { return isDeficitMargin; }
    public void setIsDeficitMargin(Boolean deficitMargin) { isDeficitMargin = deficitMargin; }
    public Boolean getForceOverride() { return forceOverride; }
    public void setForceOverride(Boolean forceOverride) { this.forceOverride = forceOverride; }
}