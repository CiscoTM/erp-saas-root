package es.tk3.operations.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.tk3.common.dto.BookingEventDTO;
import es.tk3.common.tenant.TenantContext;
import es.tk3.operations.service.FunctionSheetService;
import org.springframework.kafka.annotation.KafkaListener;
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

    @KafkaListener(topics = "sales.bookings", groupId = "operations-group-v4")
    public void consumeBookingEvent(String rawMessage) {
        try {
            // 1. Limpieza de comillas extras si existen
            String json = rawMessage;
            if (json.startsWith("\"") && json.endsWith("\"")) {
                json = objectMapper.readValue(json, String.class);
            }

            // 2. Deserializar el DTO
            BookingEventDTO event = objectMapper.readValue(json, BookingEventDTO.class);

            // 3. ESTABLECER EL CONTEXTO USANDO EL DTO (Más fiable que el Header)
            if (event.getTenantId() != null) {
                TenantContext.setTenantId(event.getTenantId());
            } else {
                throw new RuntimeException("El evento no contiene un tenantId válido");
            }

            logger.info(">>> [KAFKA] Procesando " + event.getEventType() + " para: " + event.getEventName());

            // 4. Lógica de negocio
            if ("BOOKING_CONFIRMED".equals(event.getEventType())) {
                functionSheetService.createFunctionSheetFromEvent(event);
                logger.info("✅ Hoja de servicio creada con éxito para " + event.getTenantId());
            }

        } catch (Exception e) {
            logger.severe("❌ ERROR EN CONSUMER: " + e.getMessage());
            e.printStackTrace();
            // ¡CRÍTICO! Debes relanzar la excepción para que Kafka sepa que falló
            // y pueda reintentar o enviar a una Dead Letter Topic (DLT).
            throw new RuntimeException("Error procesando evento de Kafka", e);
        } finally {
            TenantContext.clear();
        }
    }
}