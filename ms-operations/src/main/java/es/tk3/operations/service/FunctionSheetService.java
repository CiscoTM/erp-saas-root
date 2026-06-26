package es.tk3.operations.service;

import es.tk3.operations.dto.BookingEventDTO;
import es.tk3.operations.dto.BookingLineEventDTO;
import es.tk3.operations.dto.FunctionSheetDetailRequest;
import es.tk3.operations.enums.Profitability;
import es.tk3.operations.model.*;
import es.tk3.operations.repository.*;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class FunctionSheetService {

    private static final Logger logger = Logger.getLogger(FunctionSheetService.class.getName());

    private final FunctionSheetRepository repository;
    private final ProcessedEventRepository processedEventRepository;
    private final FunctionSheetDetailRepository detailRepository;
    private final DishReferenceRepository dishReferenceRepository;
    private final CommercialMenuRecipeRepository commercialMenuRecipeRepository;

    public FunctionSheetService(
            FunctionSheetRepository repository,
            ProcessedEventRepository processedEventRepository,
            FunctionSheetDetailRepository detailRepository,
            DishReferenceRepository dishReferenceRepository,
            CommercialMenuRecipeRepository commercialMenuRecipeRepository) {
        this.repository = repository;
        this.processedEventRepository = processedEventRepository;
        this.detailRepository = detailRepository;
        this.dishReferenceRepository = dishReferenceRepository;
        this.commercialMenuRecipeRepository = commercialMenuRecipeRepository;
    }

    @Transactional
    public void createFunctionSheetFromEvent(BookingEventDTO event) {
        if (processedEventRepository.existsById(event.getEventId())) return;

        if (repository.findByBookingId(event.getBookingId()).isPresent()) {
            registrarEventoProcesado(event.getEventId(), event.getEventType());
            return;
        }

        FunctionSheet sheet = new FunctionSheet();
        sheet.setBookingId(event.getBookingId());
        sheet.setEventName(event.getEventName());
        sheet.setEventDate(event.getStartDate());
        sheet.setGuestsCount(event.getGuests());
        sheet.setRoomName(event.getRoomName());
        sheet.setStatus("CONFIRMED");
        sheet.setNotes("Generada con desglose inteligente de opciones y control de raciones");

        FunctionSheet savedSheet = repository.save(sheet);

        if (event.getLines() != null) {
            for (BookingLineEventDTO line : event.getLines()) {
                List<CommercialMenuRecipe> mappings = commercialMenuRecipeRepository.findByCommercialMenuId(line.getCommercialMenuId());

                for (CommercialMenuRecipe mapping : mappings) {

                    int paxForThisDish;
                    if (line.getDishSelections() != null && line.getDishSelections().containsKey(mapping.getDish().getId().toString())) {
                        paxForThisDish = line.getDishSelections().get(mapping.getDish().getId().toString());
                    } else {
                        paxForThisDish = line.getPaxCount();
                    }

                    int dinersPerPlate = (mapping.getDish().getDinersPerPlate() != null && mapping.getDish().getDinersPerPlate() > 0)
                            ? mapping.getDish().getDinersPerPlate()
                            : 1;

                    int quantityToProduce = (int) Math.ceil((double) (paxForThisDish * mapping.getDefaultQuantity()) / dinersPerPlate);

                    if (quantityToProduce <= 0) continue;

                    FunctionSheetDetail detail = new FunctionSheetDetail();
                    detail.setFunctionSheet(savedSheet);
                    detail.setDish(mapping.getDish());
                    detail.setQuantity(quantityToProduce);
                    detail.setCustomName(mapping.getDish().getName());
                    detail.setUnitCostAtEvent(mapping.getDish().getBaseCost());
                    detail.setFinalPrice(mapping.getDish().getBaseCost());

                    detailRepository.save(detail);
                }
            }
        }

        calculateProfitability(savedSheet);
        repository.save(savedSheet);
        registrarEventoProcesado(event.getEventId(), event.getEventType());
    }

    @Transactional
    public void cancelFunctionSheet(BookingEventDTO event) {
        if (processedEventRepository.existsById(event.getEventId())) {
            logger.warning("⚠️ Cancelación " + event.getEventId() + " ya procesada.");
            return;
        }

        repository.findByBookingId(event.getBookingId()).ifPresentOrElse(sheet -> {
            sheet.setStatus("CANCELLED");
            sheet.setNotes(sheet.getNotes() + " | Cancelada vía sistema el " + LocalDateTime.now());
            repository.save(sheet);
            logger.info("✅ ÉXITO: Hoja de Servicio #" + sheet.getId() + " marcada como CANCELADA.");
        }, () -> {
            logger.warning("⚠️ Intento de cancelar hoja inexistente para booking: " + event.getBookingId());
        });

        registrarEventoProcesado(event.getEventId(), event.getEventType());
    }

    @Transactional
    public FunctionSheetDetail addDetailToSheet(UUID sheetId, FunctionSheetDetailRequest request) {
        FunctionSheet sheet = repository.findById(sheetId)
                .orElseThrow(() -> new ResourceNotFoundException("Hoja no encontrada: " + sheetId));

        Dish dish = dishReferenceRepository.findById(request.getDishId())
                .orElseThrow(() -> new ResourceNotFoundException("Plato no encontrado en catálogo local"));

        FunctionSheetDetail detail = new FunctionSheetDetail();
        detail.setFunctionSheet(sheet);
        detail.setDish(dish);
        detail.setQuantity(request.getQuantity());
        detail.setCustomName(request.getCustomName() != null ? request.getCustomName() : dish.getName());
        detail.setFinalPrice(request.getFinalPrice() != null ? request.getFinalPrice() : dish.getBaseCost());

        return detailRepository.save(detail);
    }

    private void calculateProfitability(FunctionSheet sheet){
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalRevenue = BigDecimal.ZERO;

        if(sheet.getDetails() != null){
            for(FunctionSheetDetail detail : sheet.getDetails()){
                if (detail.getUnitCostAtEvent() == null
                        || detail.getFinalPrice() == null
                        || detail.getQuantity() == null
                        || detail.getQuantity() <= 0){
                    continue;
                }
                BigDecimal quantityMultiplier = BigDecimal.valueOf(detail.getQuantity());

                BigDecimal lineCost = detail.getUnitCostAtEvent().multiply(quantityMultiplier);
                BigDecimal lineRevenue = detail.getFinalPrice().multiply(quantityMultiplier);

                totalCost = totalCost.add(lineCost);
                totalRevenue = totalRevenue.add(lineRevenue);
            }
        }

        if(totalRevenue.compareTo(BigDecimal.ZERO) == 0){
            if(totalCost.compareTo(BigDecimal.ZERO) > 0){
                sheet.setProfitabilityStatus(Profitability.RISK);
            } else {
                sheet.setProfitabilityStatus(Profitability.OPTIMAL);
            }
            return;
        }
        BigDecimal profit = totalRevenue.subtract(totalCost);
        BigDecimal margin = profit.divide(totalRevenue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        Profitability calculatedStatus;
        if(margin.compareTo(BigDecimal.valueOf(15.0)) < 0){
            calculatedStatus = Profitability.RISK;
        } else if (margin.compareTo(BigDecimal.valueOf(30.0)) < 0){
            calculatedStatus = Profitability.WARNING;
        } else {
            calculatedStatus = Profitability.OPTIMAL;
        }

        sheet.setProfitabilityStatus(calculatedStatus);
    }

    private void registrarEventoProcesado(UUID eventId, String type) {
        ProcessedEvent pe = ProcessedEvent.builder()
                .eventId(eventId)
                .processedAt(LocalDateTime.now())
                .eventType(type)
                .build();
        processedEventRepository.save(pe);
    }

    public void reevaluateSheetProfitability(FunctionSheet sheet, OperationalParameter currentParams, BookingLineEventDTO bookingLineSnapshot) {
        BigDecimal fixedSellingPrice = sheet.getAgreedMenuPrice();
        BigDecimal currentTotalCost = calculateCurrentSheetCost(sheet);
        BigDecimal totalRevenue = fixedSellingPrice.multiply(BigDecimal.valueOf(sheet.getGuestsCount()));

        if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal currentMargin = totalRevenue.subtract(currentTotalCost)
                    .divide(totalRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));

            BigDecimal riskThreshold = bookingLineSnapshot != null && bookingLineSnapshot.getSnapshotRiskMargin() != null
                    ? bookingLineSnapshot.getSnapshotRiskMargin()
                    : currentParams.getRiskMarginThreshold();

            BigDecimal optimalThreshold = bookingLineSnapshot != null && bookingLineSnapshot.getSnapshotOptimalMargin() != null
                    ? bookingLineSnapshot.getSnapshotOptimalMargin()
                    : currentParams.getOptimalMarginThreshold();

            if (currentMargin.compareTo(riskThreshold) < 0) {
                sheet.setProfitabilityStatus(Profitability.RISK);
            } else if (currentMargin.compareTo(optimalThreshold) >= 0) {
                sheet.setProfitabilityStatus(Profitability.OPTIMAL);
            } else {
                sheet.setProfitabilityStatus(Profitability.WARNING);
            }
            repository.save(sheet);
        }
    }

    private BigDecimal calculateCurrentSheetCost(FunctionSheet sheet) {
        if (sheet.getDetails() == null || sheet.getDetails().isEmpty()) {
            return BigDecimal.ZERO;
        }

        return sheet.getDetails().stream()
                .filter(detail -> detail.getUnitCostAtEvent() != null && detail.getQuantity() != null)
                .map(detail -> {
                    BigDecimal quantity = BigDecimal.valueOf(detail.getQuantity());
                    return detail.getUnitCostAtEvent().multiply(quantity);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}