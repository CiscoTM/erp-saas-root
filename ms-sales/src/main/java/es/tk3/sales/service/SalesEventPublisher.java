package es.tk3.sales.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.tk3.common.outbox.service.OutboxEventService;
import es.tk3.common.tenant.TenantContext;
import es.tk3.sales.dto.BookingEventDTO;
import es.tk3.sales.dto.BookingLineEventDTO;
import es.tk3.sales.dto.FinancialAlertEventDTO;
import es.tk3.sales.model.Booking;
import es.tk3.sales.model.BookingLine;
import es.tk3.sales.model.BookingLineSelection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class SalesEventPublisher {

    private final ObjectMapper mapper;
    private final OutboxEventService outboxEventService;

    @Value("${app.kafka.topics.financial-alerts}")
    private String financialAlertsTopic;

    @Value("${app.kafka.topics.booking-events}")
    private String SalesBookingEvent;

    public SalesEventPublisher(ObjectMapper mapper, OutboxEventService outboxEventService) {
        this.mapper = mapper;
        this.outboxEventService = outboxEventService;
    }

    public void publishBookingConfirmed(Booking booking, String eventType){
        try {
            BookingEventDTO eventDTO = new BookingEventDTO();
            eventDTO.setId(booking.getId());
            eventDTO.setEventName(booking.getEventName());
            eventDTO.setStartDate(booking.getStartDate());
            eventDTO.setEndDate(booking.getEndDate());
            eventDTO.setTotalPrice(booking.getTotalPrice());
            eventDTO.setRoomName(booking.getRoom().getName());
            eventDTO.setStatus(booking.getStatus().name());
            eventDTO.setTenantId(TenantContext.getTenantId());
            eventDTO.setEventType(eventType);

            if(booking.getLines() != null){
                eventDTO.setLines(booking.getLines().stream().map(
                        line -> {
                            BookingLineEventDTO lineDto = new BookingLineEventDTO();
                            lineDto.setCommercialMenuId(line.getCommercialMenuId());
                            lineDto.setPaxCount(line.getPaxCount());
                            lineDto.setAgreedPricePerPax(line.getAgreedPricePerPax());

                            if (line.getSelections() != null && !line.getSelections().isEmpty()) {
                                java.util.Map<String, Integer> selectionsMap = line.getSelections().stream()
                                        .collect(java.util.stream.Collectors.toMap(
                                                BookingLineSelection::getDishId,
                                                BookingLineSelection::getQuantity
                                        ));
                                lineDto.setDishSelections(selectionsMap);
                            }

                            return lineDto;
                        }
                ).toList());
            }
            String jsonPayload = mapper.writeValueAsString(eventDTO);

            outboxEventService.createAndSaveEvent(
                    booking.getId().toString(),
                    "Booking",
                    SalesBookingEvent,
                    eventType,
                    jsonPayload
                    );

        } catch (JsonProcessingException e){
            throw new RuntimeException("Error serializando evento.", e);
        }
    }

    public void publishDeficitMarginAlert(Booking booking, BookingLine line, BigDecimal priceFloor, String username){
        try {
            FinancialAlertEventDTO alert = new FinancialAlertEventDTO();
            alert.setTenantId(TenantContext.getTenantId());
            alert.setBookingId(booking.getId());
            alert.setEventName(booking.getEventName());
            alert.setCommercialMenuId(line.getCommercialMenuId());
            alert.setPaxCount(line.getPaxCount());
            alert.setAgreedPricePerPax(BigDecimal.valueOf(line.getAgreedPricePerPax()));
            alert.setMinimumRequiredPriceFloor(priceFloor);
            alert.setSalesRepUsername(username);
            alert.setAlertTimestamp(LocalDateTime.now());

            String jsonPayload = mapper.writeValueAsString(alert);
            outboxEventService.createAndSaveEvent(
                    booking.getId().toString(),
                    "FinancialAlert",
                    financialAlertsTopic,
                    "DEFICIT_MARGIN_APPROVED",
                    jsonPayload
            );


        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializando alerta financiera.", e);
        }
    }


}
