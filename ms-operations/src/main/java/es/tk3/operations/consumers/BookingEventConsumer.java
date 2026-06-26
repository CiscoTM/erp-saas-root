package es.tk3.operations.consumers;

import es.tk3.common.tenant.TenantContext;
import es.tk3.operations.dto.BookingEventDTO;
import es.tk3.operations.service.FunctionSheetService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import java.util.logging.Logger;

@Component
public class BookingEventConsumer {

    private static final Logger logger = Logger.getLogger(BookingEventConsumer.class.getName());
    private final FunctionSheetService functionSheetService;
    private final ObjectMapper objectMapper;

    public BookingEventConsumer(FunctionSheetService functionSheetService, ObjectMapper objectMapper) {
        this.functionSheetService = functionSheetService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.booking-events}",
            groupId = "${app.kafka.groups.booking-processor}"
    )
    public void consumeBookingEvent(
            String rawMessage,
            @Header(KafkaHeaders.OFFSET) long offset,      // 👁️ Captura el Offset
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition   // 👁️ Captura la Partición
    ) {
        try {
            String json = rawMessage;
            if (json.startsWith("\"") && json.endsWith("\"")) {
                json = objectMapper.readValue(json, String.class);
            }

            BookingEventDTO event = objectMapper.readValue(json, BookingEventDTO.class);

            if (event.getTenantId() != null && !event.getTenantId().trim().isEmpty()) {
                TenantContext.setTenantId(event.getTenantId());
                logger.info(">>> [KAFKA] Contexto establecido para tenant: " + event.getTenantId());
            } else {
                logger.severe("❌ [KAFKA] MENSAJE DESCARTADO [Partición: " + partition + ", Offset: " + offset + "]: Se recibió un evento con ID " + event.getEventId() + " pero no contiene TenantId.");
                return;
            }
            String type = event.getEventType();
            if ("BOOKING_CONFIRMED".equals(type)) {
                functionSheetService.createFunctionSheetFromEvent(event);
            }
            else if ("BOOKING_CANCELLED".equals(type)) {
                functionSheetService.cancelFunctionSheet(event);
            }
            else {
                logger.warning(">>> [KAFKA] Evento ignorado (Tipo no soportado): " + type);
            }

        } catch (Exception e) {
            logger.severe("❌ ERROR EN CONSUMER: " + e.getMessage() + " Tenant: " + TenantContext.getTenantId());
            throw new RuntimeException("Error procesando evento de Kafka", e);
        } finally {
            TenantContext.clear();
        }
    }
}