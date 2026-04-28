package es.tk3.operations.service;


import es.tk3.common.dto.BookingEventDTO;
import es.tk3.operations.entities.FunctionSheet;
import es.tk3.operations.repository.FunctionSheetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.logging.Logger;

@Service
public class FunctionSheetService {

    private static final Logger logger = Logger.getLogger(FunctionSheetService.class.getName());
    private final FunctionSheetRepository repository;

    public FunctionSheetService(FunctionSheetRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void createFunctionSheetFromEvent(BookingEventDTO event) {
        // 1. Verificación de Idempotencia Funcional
        if (repository.findByBookingId(event.getBookingId()).isPresent()) {
            logger.info("Hoja de servicio ya existe para el booking: " + event.getBookingId() + ". Ignorando.");
            return;
        }

        // 2. Mapeo de DTO a Entidad
        FunctionSheet sheet = new FunctionSheet();
        sheet.setBookingId(event.getBookingId());
        sheet.setEventName(event.getEventName());
        sheet.setEventDate(event.getStartDate());
        sheet.setGuestsCount(event.getGuests());
        sheet.setRoomName(event.getRoomName());
        sheet.setStatus("CONFIRMED");
        sheet.setNotes("Generada automáticamente desde ms-sales");

        repository.save(sheet);
        logger.info("Nueva Hoja de Servicio creada para el evento: " + event.getEventName());
    }
}