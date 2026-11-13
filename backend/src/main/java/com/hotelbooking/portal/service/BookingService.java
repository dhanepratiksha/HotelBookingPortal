package com.hotelbooking.portal.service;

import com.hotelbooking.portal.dto.BookingRequestDTO;
import com.hotelbooking.portal.dto.BookingResponseDTO;
import com.hotelbooking.portal.dto.PriceCalculationDTO;
import com.hotelbooking.portal.entity.Booking;
import com.hotelbooking.portal.entity.BookingStatus;
import com.hotelbooking.portal.entity.Hotel;
import com.hotelbooking.portal.entity.Room;
import com.hotelbooking.portal.exception.BookingException;
import com.hotelbooking.portal.exception.ResourceNotFoundException;
import com.hotelbooking.portal.exception.RoomUnavailableException;
import com.hotelbooking.portal.repository.BookingRepository;
import com.hotelbooking.portal.repository.HotelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Service managing room reservations, backend price calculations,
 * availability enforcement, and booking cancellations.
 */
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final HotelService hotelService;

    public BookingService(BookingRepository bookingRepository,
                          HotelRepository hotelRepository,
                          HotelService hotelService) {
        this.bookingRepository = bookingRepository;
        this.hotelRepository = hotelRepository;
        this.hotelService = hotelService;
    }

    /**
     * Calculates the booking price and available room count on the backend.
     * Formula: total amount = number of nights * room price per night.
     */
    @Transactional(readOnly = true)
    public PriceCalculationDTO calculatePriceAndAvailability(Long hotelId, LocalDate checkInDate, LocalDate checkOutDate) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId));

        hotelService.validateDates(checkInDate, checkOutDate);

        long numberOfNights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        BigDecimal totalAmount = hotel.getPricePerNight().multiply(BigDecimal.valueOf(numberOfNights));

        int availableRooms = hotelService.calculateAvailableRooms(hotelId, checkInDate, checkOutDate);

        return new PriceCalculationDTO(
                hotel.getId(),
                hotel.getName(),
                checkInDate,
                checkOutDate,
                numberOfNights,
                hotel.getPricePerNight(),
                totalAmount,
                availableRooms
        );
    }

    /**
     * Creates a new booking after strictly validating availability and calculating total amount on the backend.
     */
    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO request) {
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + request.getHotelId()));

        hotelService.validateDates(request.getCheckInDate(), request.getCheckOutDate());

        // Check availability to prevent overbooking
        int availableRooms = hotelService.calculateAvailableRooms(
                hotel.getId(), request.getCheckInDate(), request.getCheckOutDate());

        if (availableRooms <= 0) {
            throw new RoomUnavailableException("No rooms are available at " + hotel.getName() + " for the selected dates.");
        }

        // Calculate nights and total amount strictly on the backend
        long numberOfNights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        BigDecimal totalAmount = hotel.getPricePerNight().multiply(BigDecimal.valueOf(numberOfNights));

        // Assign an available physical room
        List<Long> bookedRoomIds = bookingRepository.findBookedRoomIdsForDates(
                hotel.getId(), request.getCheckInDate(), request.getCheckOutDate(), BookingStatus.CONFIRMED);

        Room assignedRoom = hotel.getRooms().stream()
                .filter(room -> !bookedRoomIds.contains(room.getId()))
                .findFirst()
                .orElse(null);

        String bookingReference = generateUniqueBookingReference();

        Booking booking = new Booking(
                bookingReference,
                hotel,
                assignedRoom,
                request.getGuestName().trim(),
                request.getGuestEmail().trim().toLowerCase(),
                request.getCheckInDate(),
                request.getCheckOutDate(),
                numberOfNights,
                hotel.getPricePerNight(),
                totalAmount
        );

        Booking savedBooking = bookingRepository.save(booking);

        return mapToResponseDTO(savedBooking);
    }

    /**
     * Retrieve a booking by its unique reference and guest email.
     */
    @Transactional(readOnly = true)
    public BookingResponseDTO getBooking(String bookingReference, String guestEmail) {
        Booking booking = bookingRepository.findByBookingReference(bookingReference.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("No booking found with reference: " + bookingReference));

        if (guestEmail != null && !guestEmail.trim().isEmpty()) {
            if (!booking.getGuestEmail().equalsIgnoreCase(guestEmail.trim())) {
                throw new BookingException("Email address does not match the booking records.");
            }
        }

        return mapToResponseDTO(booking);
    }

    /**
     * Cancel an active booking and release room availability.
     */
    @Transactional
    public BookingResponseDTO cancelBooking(String bookingReference, String guestEmail) {
        Booking booking = bookingRepository.findByBookingReference(bookingReference.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("No booking found with reference: " + bookingReference));

        if (guestEmail == null || guestEmail.trim().isEmpty()) {
            throw new BookingException("Email address is required to cancel a booking.");
        }

        if (!booking.getGuestEmail().equalsIgnoreCase(guestEmail.trim())) {
            throw new BookingException("Email address does not match the booking records.");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BookingException("This booking is already cancelled.");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking updatedBooking = bookingRepository.save(booking);

        return mapToResponseDTO(updatedBooking);
    }

    private String generateUniqueBookingReference() {
        return "HB-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    private BookingResponseDTO mapToResponseDTO(Booking booking) {
        String roomNumber = booking.getRoom() != null ? booking.getRoom().getRoomNumber() : "Standard";
        return new BookingResponseDTO(
                booking.getBookingReference(),
                booking.getHotel().getId(),
                booking.getHotel().getName(),
                booking.getHotel().getLocation(),
                booking.getGuestName(),
                booking.getGuestEmail(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getNumberOfNights(),
                booking.getRoomPricePerNight(),
                booking.getTotalAmount(),
                booking.getStatus().name(),
                booking.getCreatedAt(),
                roomNumber
        );
    }
}
