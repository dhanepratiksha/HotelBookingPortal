package com.hotelbooking.portal.service;

import com.hotelbooking.portal.dto.BookingRequestDTO;
import com.hotelbooking.portal.dto.BookingResponseDTO;
import com.hotelbooking.portal.dto.PriceCalculationDTO;
import com.hotelbooking.portal.entity.BookingStatus;
import com.hotelbooking.portal.entity.Hotel;
import com.hotelbooking.portal.exception.BookingException;
import com.hotelbooking.portal.exception.InvalidBookingDateException;
import com.hotelbooking.portal.exception.RoomUnavailableException;
import com.hotelbooking.portal.repository.HotelRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Automated tests covering core business requirements:
 * 1. Price calculation on the backend
 * 2. Date validations (past dates, invalid range)
 * 3. Room availability & overlapping reservation prevention
 * 4. Cancellation and release of room availability
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private HotelRepository hotelRepository;

    @Test
    @DisplayName("Backend should accurately calculate number of nights and total amount")
    void shouldCalculatePriceCorrectlyOnBackend() {
        Hotel hotel = hotelRepository.findAll().get(0);
        LocalDate checkIn = LocalDate.now().plusDays(2);
        LocalDate checkOut = LocalDate.now().plusDays(5); // 3 nights

        PriceCalculationDTO priceCalc = bookingService.calculatePriceAndAvailability(
                hotel.getId(), checkIn, checkOut);

        assertEquals(3L, priceCalc.getNumberOfNights(), "Should calculate exactly 3 nights");

        BigDecimal expectedTotal = hotel.getPricePerNight().multiply(BigDecimal.valueOf(3));
        assertEquals(expectedTotal, priceCalc.getTotalAmount(), "Total amount must equal nights * pricePerNight");
    }

    @Test
    @DisplayName("Should reject check-in date in the past")
    void shouldRejectPastCheckInDate() {
        Hotel hotel = hotelRepository.findAll().get(0);
        LocalDate pastCheckIn = LocalDate.now().minusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(2);

        assertThrows(InvalidBookingDateException.class, () ->
                hotelService.validateDates(pastCheckIn, checkOut),
                "Should throw InvalidBookingDateException for past check-in");
    }

    @Test
    @DisplayName("Should reject check-out date that is before or equal to check-in date")
    void shouldRejectCheckOutBeforeOrEqualToCheckIn() {
        LocalDate checkIn = LocalDate.now().plusDays(3);
        LocalDate invalidCheckOut = LocalDate.now().plusDays(3); // Same day (0 nights)

        assertThrows(InvalidBookingDateException.class, () ->
                hotelService.validateDates(checkIn, invalidCheckOut),
                "Should throw InvalidBookingDateException when check-out is equal to check-in");
    }

    @Test
    @DisplayName("Should book room and prevent overbooking when capacity is reached")
    void shouldPreventOverbookingWhenCapacityReached() {
        // Find hotel with small capacity (e.g. 3 rooms)
        Hotel hotel = hotelRepository.findAll().stream()
                .filter(h -> h.getTotalRooms() == 3)
                .findFirst()
                .orElse(hotelRepository.findAll().get(0));

        int capacity = hotel.getTotalRooms();
        LocalDate checkIn = LocalDate.now().plusDays(10);
        LocalDate checkOut = LocalDate.now().plusDays(13);

        // Book all available rooms
        for (int i = 0; i < capacity; i++) {
            BookingRequestDTO req = new BookingRequestDTO(
                    hotel.getId(),
                    "Guest " + i,
                    "guest" + i + "@example.com",
                    checkIn,
                    checkOut
            );
            BookingResponseDTO response = bookingService.createBooking(req);
            assertNotNull(response.getBookingReference());
            assertEquals(BookingStatus.CONFIRMED.name(), response.getStatus());
        }

        // Available rooms should now be 0
        int remaining = hotelService.calculateAvailableRooms(hotel.getId(), checkIn, checkOut);
        assertEquals(0, remaining, "No rooms should be available once all rooms are booked");

        // Attempting an extra overlapping booking must fail
        BookingRequestDTO overbookingReq = new BookingRequestDTO(
                hotel.getId(),
                "Overbooking Guest",
                "overbooking@example.com",
                checkIn,
                checkOut
        );

        assertThrows(RoomUnavailableException.class, () ->
                bookingService.createBooking(overbookingReq),
                "Must throw RoomUnavailableException when no rooms remain");
    }

    @Test
    @DisplayName("Cancelling a booking must release availability immediately")
    void shouldReleaseAvailabilityOnCancellation() {
        Hotel hotel = hotelRepository.findAll().get(0);
        LocalDate checkIn = LocalDate.now().plusDays(20);
        LocalDate checkOut = LocalDate.now().plusDays(23);

        int initialAvailable = hotelService.calculateAvailableRooms(hotel.getId(), checkIn, checkOut);

        BookingRequestDTO req = new BookingRequestDTO(
                hotel.getId(),
                "Alice Wonder",
                "alice@example.com",
                checkIn,
                checkOut
        );
        BookingResponseDTO booking = bookingService.createBooking(req);

        // After booking, available count decreases by 1
        int afterBooking = hotelService.calculateAvailableRooms(hotel.getId(), checkIn, checkOut);
        assertEquals(initialAvailable - 1, afterBooking, "Available rooms must decrease by 1 after booking");

        // Cancel the booking
        BookingResponseDTO cancelled = bookingService.cancelBooking(booking.getBookingReference(), "alice@example.com");
        assertEquals(BookingStatus.CANCELLED.name(), cancelled.getStatus());

        // After cancellation, availability must be restored
        int afterCancellation = hotelService.calculateAvailableRooms(hotel.getId(), checkIn, checkOut);
        assertEquals(initialAvailable, afterCancellation, "Available rooms must be released back after cancellation");
    }

    @Test
    @DisplayName("Cannot cancel an already cancelled booking or with incorrect email")
    void shouldRejectInvalidCancellation() {
        Hotel hotel = hotelRepository.findAll().get(0);
        LocalDate checkIn = LocalDate.now().plusDays(30);
        LocalDate checkOut = LocalDate.now().plusDays(33);

        BookingRequestDTO req = new BookingRequestDTO(
                hotel.getId(),
                "Bob Builder",
                "bob@example.com",
                checkIn,
                checkOut
        );
        BookingResponseDTO booking = bookingService.createBooking(req);

        // Cancel with wrong email should fail
        assertThrows(BookingException.class, () ->
                bookingService.cancelBooking(booking.getBookingReference(), "wrong@example.com"));

        // Cancel with correct email succeeds
        bookingService.cancelBooking(booking.getBookingReference(), "bob@example.com");

        // Second cancellation attempt must fail
        assertThrows(BookingException.class, () ->
                bookingService.cancelBooking(booking.getBookingReference(), "bob@example.com"),
                "Should fail when trying to cancel an already cancelled booking");
    }
}
