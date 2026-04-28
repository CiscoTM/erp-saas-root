package es.tk3.sales.service;

import es.tk3.sales.model.Event;
import es.tk3.sales.model.Outbox;
import es.tk3.sales.repository.EventRepository;
import es.tk3.sales.repository.OutboxRepository;
import es.tk3.common.tenant.TenantContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class SalesService {

    private final EventRepository eventRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public SalesService(EventRepository eventRepository,
                        OutboxRepository outboxRepository,
                        ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Event saveEventWithOutbox(Event event) {
        // 1. Guardar el evento en la DB del Tenant
        Event savedEvent = eventRepository.save(event);

        // 2. Crear el registro en la tabla Outbox
        Outbox outbox = new Outbox();
        outbox.setAggregateId(savedEvent.getId().toString());
        outbox.setType("BOOKING_CONFIRMED");
        outbox.setTenantId(TenantContext.getTenantId());

        // El traceId se conectará con Zipkin/Micrometer más adelante
        outbox.setTraceId(UUID.randomUUID().toString());

        try {
            String payload = objectMapper.writeValueAsString(savedEvent);
            outbox.setPayload(payload);
        } catch (Exception e) {
            throw new RuntimeException("Error al serializar evento para Outbox", e);
        }

        // 3. Persistir Outbox (Si esto falla, se hace rollback de la reserva)
        outboxRepository.save(outbox);

        return savedEvent;
    }
}