package es.tk3.sales.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "booking_lines")
public class BookingLine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Booking booking;
    @Column(name = "commercial_menu_id")
    private String commercialMenuId;
    @Column(name = "pax_count")
    private Integer paxCount;
    @Column(name = "agreed_price_per_pax")
    private Double agreedPricePerPax;
    @OneToMany(mappedBy = "bookingLine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingLineSelection> selections = new ArrayList<>();

    @Column(name = "snapshot_overhead_percentage")
    private BigDecimal snapshotOverheadPercentage;

    @Column(name = "snapshot_risk_margin_threshold")
    private BigDecimal snapshotRiskMarginThreshold;

    @Column(name = "snapshot_optimal_margin_threshold")
    private BigDecimal snapshotOptimalMarginThreshold;

    public void addSelection(BookingLineSelection selection) {
        selections.add(selection);
        selection.setBookingLine(this);
    }

    public List<BookingLineSelection> getSelections() { return selections; }
    public void setSelections(List<BookingLineSelection> selections) { this.selections = selections; }

    public Double getLineTotal() { return paxCount * agreedPricePerPax; }

    public Booking getBooking() { return booking; }
    public String getCommercialMenuId() { return commercialMenuId; }
    public Integer getPaxCount() { return paxCount; }
    public Double getAgreedPricePerPax() { return agreedPricePerPax; }

    public void setBooking(Booking booking) { this.booking = booking; }
    public void setCommercialMenuId(String cid) { this.commercialMenuId = cid; }
    public void setPaxCount(Integer count) { this.paxCount = count; }
    public void setAgreedPricePerPax(Double price) { this.agreedPricePerPax = price; }

    public BigDecimal getSnapshotOverheadPercentage() { return snapshotOverheadPercentage; }
    public void setSnapshotOverheadPercentage(BigDecimal snapshotOverheadPercentage) { this.snapshotOverheadPercentage = snapshotOverheadPercentage; }
    public BigDecimal getSnapshotRiskMarginThreshold() { return snapshotRiskMarginThreshold; }
    public void setSnapshotRiskMarginThreshold(BigDecimal snapshotRiskMarginThreshold) { this.snapshotRiskMarginThreshold = snapshotRiskMarginThreshold; }
    public BigDecimal getSnapshotOptimalMarginThreshold() { return snapshotOptimalMarginThreshold; }
    public void setSnapshotOptimalMarginThreshold(BigDecimal snapshotOptimalMarginThreshold) { this.snapshotOptimalMarginThreshold = snapshotOptimalMarginThreshold; }
}
