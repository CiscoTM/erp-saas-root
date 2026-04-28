package es.tk3.sales.service;

import es.tk3.common.dto.BookingEventDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.tk3.common.tenant.TenantContext;
import es.tk3.sales.model.Booking;
import es.tk3.sales.model.BookingStatus;
import es.tk3.sales.model.Outbox;
import es.tk3.sales.model.Room;
import es.tk3.sales.repository.BookingRepository;
import es.tk3.sales.repository.OutboxRepository;
import es.tk3.sales.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    @Autowired private BookingRepository bookingRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private OutboxRepository outboxRepository;
    @Autowired private ObjectMapper objectMapper;

    /**
     * Crea un presupuesto y registra el evento BOOKING_CREATED.
     */
    @Transactional
    public Booking createBudget(Booking booking) {
        Room room = roomRepository.findById(booking.getRoom().getId())
                .orElseThrow(() -> new RuntimeException("Error: The room does not exist."));

        booking.setRoom(room);

        if (booking.getExpectedGuests() != null && booking.getExpectedGuests() > room.getCapacity()) {
            throw new RuntimeException("Error: Capacity exceeded.");
        }

        booking.calculatePrice();
        booking.setStatus(BookingStatus.BUDGET);
        Booking saved = bookingRepository.save(booking);

        // Disparamos evento de creación
        saveToOutbox(saved, "BOOKING_CREATED");

        log.info("Presupuesto #{} creado para tenant: {}", saved.getId(), TenantContext.getTenantId());
        return saved;
    }

    /**
     * Confirma la reserva y registra el evento BOOKING_CONFIRMED.
     */
    @Transactional
    public Booking confirmBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Booking not found"));

        // Validación de solapamientos
        List<Booking> overlaps = bookingRepository.findOverlappingBookings(
                booking.getRoom().getId(),
                booking.getStartDate(),
                booking.getEndDate()
        );

        boolean isOccupied = overlaps.stream()
                .anyMatch(b -> b.getStatus() == BookingStatus.CONFIRMED && !b.getId().equals(id));

        if (isOccupied) {
            throw new RuntimeException("Error: The room is already occupied.");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        Booking saved = bookingRepository.save(booking);

        // Disparamos evento de confirmación
        saveToOutbox(saved, "BOOKING_CONFIRMED");

        log.info("Reserva #{} confirmada para tenant: {}", id, TenantContext.getTenantId());
        return saved;
    }

    /**
     * Cancela la reserva y registra el evento BOOKING_CANCELLED.
     */
    @Transactional
    public Booking cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Booking not found"));

        booking.setStatus(BookingStatus.CANCELLED);
        Booking saved = bookingRepository.save(booking);

        // Disparamos evento de cancelación
        saveToOutbox(saved, "BOOKING_CANCELLED");

        log.info("Reserva #{} cancelada para tenant: {}", id, TenantContext.getTenantId());
        return saved;
    }

    public List<Booking> getCalendar(LocalDateTime start, LocalDateTime end) {
        return bookingRepository.findByStartDateBetween(start, end);
    }

    /**
     * Transforma la entidad Booking en un DTO de integración y lo guarda en el Outbox.
     */
    private void saveToOutbox(Booking booking, String eventType) {
        try {
            BookingEventDTO event = new BookingEventDTO();
            event.setEventId(UUID.randomUUID());
            event.setTenantId(TenantContext.getTenantId());
            event.setEventType(eventType);
            event.setBookingId(booking.getId());
            event.setEventName(booking.getEventName());
            event.setStartDate(booking.getStartDate());
            event.setGuests(booking.getExpectedGuests());
            event.setRoomName(booking.getRoom() != null ? booking.getRoom().getName() : null);

            Outbox outbox = new Outbox();
            outbox.setId(event.getEventId());
            outbox.setAggregateId(booking.getId().toString());
            outbox.setType(eventType);
            outbox.setTenantId(event.getTenantId());
            outbox.setStatus("PENDING");
            outbox.setPayload(objectMapper.writeValueAsString(event));

            outboxRepository.save(outbox);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing event", e);
        }
    }
}