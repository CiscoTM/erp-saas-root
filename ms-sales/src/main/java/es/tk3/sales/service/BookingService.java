package es.tk3.sales.service;

import es.tk3.sales.dto.BookingDTO;
import es.tk3.sales.dto.BookingLineDTO;
import es.tk3.sales.dto.CustomerDTO;
import es.tk3.sales.dto.PricingCorridorDTO;
import es.tk3.sales.model.*;
import es.tk3.sales.repository.BookingRepository;
import es.tk3.sales.repository.CustomerRepository;
import es.tk3.sales.repository.EventTypeRepository;
import es.tk3.sales.repository.RoomRepository;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final SalesEventPublisher salesEventPublisher;
    private final EventTypeRepository eventTypeRepository;
    private final CustomerRepository customerRepository;
    private final PricingSimulationService pricingSimulationService;

    public BookingService(
            BookingRepository bookingRepository,
            RoomRepository roomRepository,
            SalesEventPublisher salesEventPublisher,
            EventTypeRepository eventTypeRepository,
            CustomerRepository customerRepository,
            PricingSimulationService pricingSimulationService) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.salesEventPublisher = salesEventPublisher;
        this.eventTypeRepository = eventTypeRepository;
        this.customerRepository = customerRepository;
        this.pricingSimulationService = pricingSimulationService;
    }

    @Transactional
    public Booking createBudget(BookingDTO dto) {
        Booking booking = toEntity(dto);

        Room room = roomRepository.findById(booking.getRoom().getId())
                .orElseThrow(() -> new RuntimeException("Error: The room does not exist."));
        booking.setRoom(room);

        if (booking.getEventType() != null && booking.getEventType().getId() != null) {
            EventType eventType = eventTypeRepository.findById(booking.getEventType().getId())
                    .orElseThrow(() -> new RuntimeException("Error: El tipo de evento no existe."));
            booking.setEventType(eventType);
        }

        if (booking.getExpectedGuests() != null && booking.getExpectedGuests() > room.getCapacity()) {
            throw new RuntimeException("Error: Capacity exceeded.");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        boolean isSalesDirector = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLES_SALES_DIRECTOR"));

        boolean alertTriggered = false;

        for(BookingLineDTO lineDTO : dto.getLines()){
            PricingCorridorDTO corridor = pricingSimulationService.simulatePricing(lineDTO);
            BigDecimal agreedPrice = BigDecimal.valueOf(lineDTO.getAgreedPricePerPax());
            if(agreedPrice.compareTo(corridor.minimumPerPax()) < 0){
                if(Boolean.TRUE.equals(dto.getForceOverride()) && isSalesDirector){
                    alertTriggered = true;
                    booking.setIsDeficitMargin(true);
                    booking.setForceOverride(true);
                } else {
                    throw new  IllegalArgumentException(
                    "Operación rechazada: El precio pactado (" + agreedPrice + "€) " +
                    "está por debajo del suelo operativo de rentabilidad (" + corridor.minimumPerPax() + "€). " +
                    "Se requiere autorización de Dirección Comercial."
                    );
                }
            }
        }
        booking.calculatePrice();
        booking.setStatus(BookingStatus.BUDGET);
        Booking saved = bookingRepository.save(booking);

        salesEventPublisher.publishBookingConfirmed(saved, "BOOKING_CREATED");

        if(alertTriggered){
            for(BookingLine line: saved.getLines()){
                BookingLineDTO tempDTO = new BookingLineDTO();
                tempDTO.setCommercialMenuId(line.getCommercialMenuId());
                tempDTO.setPaxCount(line.getPaxCount());
                tempDTO.setDishSelections(line.getSelections().stream().collect(Collectors.toMap(
                        BookingLineSelection::getDishId,
                        BookingLineSelection::getQuantity
                )));
                tempDTO.setAgreedPricePerPax(line.getAgreedPricePerPax());

                PricingCorridorDTO corridor = pricingSimulationService.simulatePricing(tempDTO);
                if(BigDecimal.valueOf(line.getAgreedPricePerPax()).compareTo(corridor.minimumPerPax()) < 0){
                    salesEventPublisher.publishDeficitMarginAlert(saved, line, corridor.minimumPerPax(), currentUsername);
                }
            }
        }
        return saved;
    }

    @Transactional
    public Booking confirmBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Booking not found"));

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

        salesEventPublisher.publishBookingConfirmed(saved, "BOOKING_CONFIRMED");
        return saved;
    }

    @Transactional
    public Booking cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Booking not found"));

        booking.setStatus(BookingStatus.CANCELLED);
        Booking savedBooking = bookingRepository.save(booking);

        salesEventPublisher.publishBookingConfirmed(savedBooking, "BOOKING_CANCELLED");
        return savedBooking;
    }

    public List<Booking> getCalendar(LocalDateTime start, LocalDateTime end) {
        return bookingRepository.findByStartDateBetween(start, end);
    }

    public BookingDTO toDTO(Booking booking){
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setEventName(booking.getEventName());
        dto.setExpectedGuests(booking.getExpectedGuests());
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());

        dto.setForceOverride(booking.getForceOverride());

        if (booking.getCustomer() != null) {
            Customer customerEntity = booking.getCustomer();
            CustomerDTO customerDTO = new CustomerDTO(
                    customerEntity.getId(),
                    customerEntity.getTaxId(),
                    customerEntity.getName(),
                    customerEntity.getEmail(),
                    customerEntity.getCustomerType()
            );
            dto.setCustomer(customerDTO);
        }

        if(booking.getRoom() != null){
            dto.setRoomId(booking.getRoom().getId());
        }

        if(booking.getEventType() != null){
            dto.setEventTypeId(booking.getEventType().getId());
            dto.setEventTypeName(booking.getEventType().getName());
        }

        if(booking.getLines() != null){
            dto.setLines(booking.getLines().stream().map(bookingLine -> {
                BookingLineDTO lineDTO = new BookingLineDTO();
                lineDTO.setCommercialMenuId(bookingLine.getCommercialMenuId());
                lineDTO.setPaxCount(bookingLine.getPaxCount());
                lineDTO.setAgreedPricePerPax(bookingLine.getAgreedPricePerPax());

                if (bookingLine.getSelections() != null && !bookingLine.getSelections().isEmpty()) {
                    Map<String, Integer> selectionsMap = bookingLine.getSelections().stream()
                            .collect(Collectors.toMap(
                                    BookingLineSelection::getDishId,
                                    BookingLineSelection::getQuantity
                            ));
                    lineDTO.setDishSelections(selectionsMap);
                }

                return lineDTO;
            }).toList());
        }
        return dto;
    }

    public Booking toEntity(BookingDTO dto){
        Booking booking = getBooking(dto);
        CustomerDTO dtoCustomer = dto.getCustomer();
        if (dtoCustomer != null) {
            if (dtoCustomer.id() != null) {
                Customer existingCustomer = customerRepository.findById(dtoCustomer.id())
                        .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
                booking.setCustomer(existingCustomer);
            } else {
                Customer customer = new Customer();
                customer.setName(dtoCustomer.name());
                customer.setEmail(dtoCustomer.email());
                customer.setTaxId(dtoCustomer.taxId());
                customer.setCustomerType(dtoCustomer.customerType());

                Customer savedCustomer = customerRepository.save(customer);
                booking.setCustomer(savedCustomer);
            }
        }
        if(dto.getLines() != null){
            dto.getLines().forEach(lineDTO -> {
                BookingLine line = new BookingLine();
                line.setCommercialMenuId(lineDTO.getCommercialMenuId());
                line.setPaxCount(lineDTO.getPaxCount());
                line.setAgreedPricePerPax(lineDTO.getAgreedPricePerPax());

                if (lineDTO.getDishSelections() != null) {
                    lineDTO.getDishSelections().forEach((dishId, quantity) -> {
                        BookingLineSelection selection = new BookingLineSelection();
                        selection.setDishId(dishId);
                        selection.setQuantity(quantity);
                        line.addSelection(selection);
                    });
                }
                booking.addLine(line);
            } );
        }
        return booking;
    }
    private static @NonNull Booking getBooking(BookingDTO dto) {
        Booking booking = new Booking();
        booking.setEventName(dto.getEventName());
        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setExpectedGuests(dto.getExpectedGuests());
        booking.setForceOverride(dto.getForceOverride());



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