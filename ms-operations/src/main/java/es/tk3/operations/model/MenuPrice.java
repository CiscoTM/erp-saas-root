package es.tk3.operations.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "menu_prices")
public class MenuPrice {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commercial_menu_id", nullable = false)
    private CommercialMenu commercialMenu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff;

    @Column(name = "price_per_pax", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerPax;

    public MenuPrice() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public CommercialMenu getCommercialMenu() { return commercialMenu;}
    public void setCommercialMenu(CommercialMenu commercialMenu) { this.commercialMenu = commercialMenu; }

    public Season getSeason() { return season; }
    public void setSeason(Season season) { this.season = season; }

    public Tariff getTariff() { return tariff; }
    public void setTariff(Tariff tariff) { this.tariff = tariff; }

    public BigDecimal getPricePerPax() { return pricePerPax; }
    public void setPricePerPax(BigDecimal pricePerPax) { this.pricePerPax = pricePerPax; }
}
