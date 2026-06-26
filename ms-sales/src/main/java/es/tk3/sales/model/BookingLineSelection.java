package es.tk3.sales.model;

import jakarta.persistence.*;

@Entity
@Table(name = "booking_line_selections")
public class BookingLineSelection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "booking_line_id")
    private BookingLine bookingLine;
    @Column(name = "dish_id")
    private String dishId;
    private Integer quantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BookingLine getBookingLine() {
        return bookingLine;
    }

    public void setBookingLine(BookingLine bookingLine) {
        this.bookingLine = bookingLine;
    }

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
