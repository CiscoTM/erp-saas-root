package es.tk3.sales.controller;

import es.tk3.sales.model.Booking;
import es.tk3.sales.service.BookingService;
import es.tk3.sales.dto.BookingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/sales/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // Acepta /api/v1/sales/bookings y /api/v1/sales/bookings/
    @PostMapping({"", "/"})
    public ResponseEntity<BookingDTO> createBudget(@RequestBody Booking booking) {
        Booking saved = bookingService.createBudget(booking);
        // Usamos HttpStatus.CREATED (201) para nuevos recursos
        return new ResponseEntity<>(convertToDto(saved), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<BookingDTO> confirm(@PathVariable Long id) {
        Booking confirmed = bookingService.confirmBooking(id);
        return ResponseEntity.ok(convertToDto(confirmed));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingDTO> cancel(@PathVariable Long id) {
        Booking cancelled = bookingService.cancelBooking(id);
        return ResponseEntity.ok(convertToDto(cancelled));
    }

    @GetMapping("/calendar")
    public ResponseEntity<List<BookingDTO>> getCalendar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        // Ahora recibimos LocalDateTime directamente gracias a @DateTimeFormat
        List<Booking> list = bookingService.getCalendar(start, end);

        return ResponseEntity.ok(list.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList()));
    }

    private BookingDTO convertToDto(Booking b) {
        BookingDTO dto = new BookingDTO();
        dto.setId(b.getId());
        dto.setEventName(b.getEventName());
        dto.setCustomerName(b.getCustomerName());
        dto.setStartDate(b.getStartDate());
        dto.setEndDate(b.getEndDate());
        dto.setStatus(b.getStatus() != null ? b.getStatus().name() : null);
        dto.setTotalPrice(b.getTotalPrice());
        dto.setExpectedGuests(b.getExpectedGuests());
        if (b.getRoom() != null) {
            dto.setRoomName(b.getRoom().getName());
        }
        return dto;
    }
}