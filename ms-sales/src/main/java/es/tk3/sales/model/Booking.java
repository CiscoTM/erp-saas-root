package es.tk3.sales.model;

import jakarta.persistence.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "event_name") // Mapeo a event_name
    private String eventName;

    @Column(name = "customer_name") // Mapeo a customer_name
    private String customerName;

    @Column(name = "start_date") // Mapeo a start_date
    private LocalDateTime startDate;

    @Column(name = "end_date") // Mapeo a end_date
    private LocalDateTime endDate;

    @Column(name = "total_price") // Mapeo a total_price
    private Double totalPrice;

    @Column(name = "expected_guests") // Mapeo a expected_guests
    private Integer expectedGuests;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    public void calculatePrice() {
        if (room != null && startDate != null && endDate != null) {
            long hours = java.time.Duration.between(startDate, endDate).toHours();
            if (hours == 0) hours = 1; // Mínimo 1 hora
            this.totalPrice = hours * room.getPricePerHour();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getExpectedGuests() {
        return expectedGuests;
    }

    public void setExpectedGuests(Integer expectedGuests) {
        this.expectedGuests = expectedGuests;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}
