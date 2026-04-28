package es.tk3.sales.controller;

import es.tk3.sales.model.Event;
import es.tk3.sales.model.EventType;
import es.tk3.sales.repository.EventRepository;
import es.tk3.sales.repository.EventTypeRepository;
import es.tk3.sales.service.SalesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sales")
public class SalesController {

    @Autowired private EventRepository eventRepository;
    @Autowired private EventTypeRepository typeRepository;
    @Autowired private SalesService salesService; // Inyectamos el servicio

    @GetMapping("/event-types")
    public List<EventType> getTypes(){
        return typeRepository.findAll();
    }

    @PostMapping("/events")
    public ResponseEntity<?> createEvent(@Valid @RequestBody Event event){
        return typeRepository.findById(event.getEventType().getId())
                .map(existingType -> {
                    event.setEventType(existingType);

                    if (event.getEndDate().isBefore(event.getStartDate())) {
                        return ResponseEntity.badRequest().body("La fecha de fin es anterior al inicio");
                    }

                    // USAMOS EL SERVICIO TRANSACCIONAL
                    Event saved = salesService.saveEventWithOutbox(event);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.badRequest().body("El tipo de evento especificado no existe."));
    }

    @GetMapping("/events")
    public List<Event> getEvents(){
        return eventRepository.findAll();
    }
}