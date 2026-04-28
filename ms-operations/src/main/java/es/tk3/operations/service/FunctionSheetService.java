package es.tk3.operations.service;


import es.tk3.common.dto.BookingEventDTO;
import es.tk3.operations.entities.FunctionSheet;
import es.tk3.operations.entities.ProcessedEvent;
import es.tk3.operations.repository.FunctionSheetRepository;
import es.tk3.operations.repository.ProcessedEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class FunctionSheetService {

    private static final Logger logger = Logger.getLogger(FunctionSheetService.class.getName());

    private final FunctionSheetRepository repository;
    private final ProcessedEventRepository processedEventRepository;

    public FunctionSheetService(FunctionSheetRepository repository, ProcessedEventRepository processedEventRepository) {
        this.repository = repository;
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    public void createFunctionSheetFromEvent(BookingEventDTO event) {
        // 1. Verificación de Idempotencia Funcional
        if (repository.findByBookingId(event.getBookingId()).isPresent()) {
            logger.info("Hoja de servicio ya existe para el booking: " + event.getBookingId() + ". Ignorando.");
            return;
        }
        if(processedEventRepository.existsById(event.getEventId())){
            return;
        }

        // 2. Lógica de Negocio (Mapeo existente) [cite: 609]
        FunctionSheet sheet = new FunctionSheet();
        sheet.setBookingId(event.getBookingId());
        sheet.setEventName(event.getEventName());
        sheet.setEventDate(event.getStartDate());
        sheet.setGuestsCount(event.getGuests());
        sheet.setRoomName(event.getRoomName());
        sheet.setStatus("CONFIRMED");
        sheet.setNotes("Generada automáticamente desde ms-sales");

        repository.save(sheet);

        // 3. Registro del evento procesado (DENTRO DE LA MISMA TX)
        ProcessedEvent pe = ProcessedEvent.builder()
                .eventId(UUID.fromString(event.getEventId().toString()))
                .processedAt(LocalDateTime.now())
                .eventType("BOOKING_CONFIRMED")
                .build();

        processedEventRepository.save(pe);

        logger.info("Nueva Hoja de Servicio creada para el evento: " + event.getEventName());
    }
}