package es.tk3.sales.controller;

import es.tk3.sales.dto.BookingDTO;
import es.tk3.sales.dto.BookingLineDTO;
import es.tk3.sales.dto.PricingCorridorDTO;
import es.tk3.sales.model.Booking;
import es.tk3.sales.model.Customer;
import es.tk3.sales.model.EventType;
import es.tk3.sales.model.Room;
import es.tk3.sales.service.BookingService;
import es.tk3.sales.service.PricingSimulationService;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sales/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final PricingSimulationService pricingSimulationService;

    public BookingController(BookingService bookingService, PricingSimulationService pricingSimulationService) {
        this.bookingService = bookingService;
        this.pricingSimulationService = pricingSimulationService;
    }

    @PostMapping({"", "/"})
    public ResponseEntity<BookingDTO> createBudget(@Valid @RequestBody BookingDTO dto) {
        Booking booking = bookingService.toEntity(dto);
        Booking saved = bookingService.createBudget(dto);
        return new ResponseEntity<>(bookingService.toDTO(saved), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<BookingDTO> confirm(@PathVariable Long id) {
        Booking confirmed = bookingService.confirmBooking(id);
        return ResponseEntity.ok(bookingService.toDTO(confirmed));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingDTO> cancel(@PathVariable Long id) {
        Booking cancelled = bookingService.cancelBooking(id);
        return ResponseEntity.ok(bookingService.toDTO(cancelled));
    }

    @GetMapping("/calendar")
    public ResponseEntity<List<BookingDTO>> getCalendar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<Booking> list = bookingService.getCalendar(start, end);

        return ResponseEntity.ok(list.stream()
                .map(bookingService::toDTO)
                .toList());
    }

    @PostMapping("/simulate-pricing")
    @PreAuthorize("hasAnyRole('TENANT_ADMIN', 'SALES')")
    public ResponseEntity<PricingCorridorDTO> simulatePricing(@RequestBody BookingLineDTO lineDto) {
        return ResponseEntity.ok(pricingSimulationService.simulatePricing(lineDto));
    }

    private static @NonNull Booking getBooking(BookingDTO dto) {
        Booking booking = new Booking();
        booking.setEventName(dto.getEventName());
        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setExpectedGuests(dto.getExpectedGuests());

        Customer customer = new Customer();
        customer.setId(dto.getCustomer().id());
        customer.setName(dto.getCustomer().name());
        customer.setEmail(dto.getCustomer().email());
        customer.setTaxId(dto.getCustomer().taxId());
        customer.setCustomerType(dto.getCustomer().customerType());
        booking.setCustomer(customer);


        if(dto.getRoomId() != null){
            Room room = new Room();
            room.setId(dto.getRoomId());
            booking.setRoom(room);
        }
        if(dto.getEventTypeId() != null){
            EventType eventType = new EventType();
            eventType.setId(dto.getEventTypeId());
            booking.setEventType(eventType);
        }
        return booking;
    }

}