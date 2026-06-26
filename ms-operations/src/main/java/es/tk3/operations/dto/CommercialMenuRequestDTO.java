package es.tk3.operations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CommercialMenuRequestDTO {
    @NotBlank
    private String name;
    private String description;
    @NotNull private Long menuTemplateId;
    @NotNull
    private List<MenuPriceRequestDTO> prices;

    public CommercialMenuRequestDTO() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getMenuTemplateId() { return menuTemplateId; }
    public void setMenuTemplateId(Long menuTemplateId) { this.menuTemplateId = menuTemplateId; }
    public List<MenuPriceRequestDTO> getPrices() { return prices; }
    public void setPrices(List<MenuPriceRequestDTO> prices) {this.prices = prices; }

    public static class MenuPriceRequestDTO {
        @NotNull
        private UUID seasonId;
        @NotNull
        private UUID tariffId;
        @NotNull
        private BigDecimal pricePerPax;

        public MenuPriceRequestDTO() {}

        public UUID getSeasonId() { return seasonId; }
        public void setSeasonId(UUID seasonId) { this.seasonId = seasonId; }
        public UUID getTariffId() { return tariffId; }
        public void setTariffId(UUID tariffId) { this.tariffId = tariffId; }
        public BigDecimal getPricePerPax() { return pricePerPax; }
        public void setPricePerPax(BigDecimal pricePerPax) { this.pricePerPax = pricePerPax; }
    }
}
