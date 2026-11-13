package com.hotelbooking.portal.controller;

import com.hotelbooking.portal.dto.*;
import com.hotelbooking.portal.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for creating reservations, looking up bookings, and cancelling reservations.
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Create a new booking with server-side price calculation and room capacity check.
     * POST /api/bookings
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponseDTO>> createBooking(
            @Valid @RequestBody BookingRequestDTO request) {

        BookingResponseDTO bookingResponse = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Booking created successfully", bookingResponse));
    }

    /**
     * Retrieve booking by reference and guest email query parameter.
     * GET /api/bookings/{reference}?email=guest@example.com
     */
    @GetMapping("/{reference}")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> getBookingByReference(
            @PathVariable String reference,
            @RequestParam(required = false) String email) {

        BookingResponseDTO booking = bookingService.getBooking(reference, email);
        return ResponseEntity.ok(ApiResponse.success("Booking retrieved successfully", booking));
    }

    /**
     * Dedicated POST endpoint for frontend retrieval form submission.
     * POST /api/bookings/retrieve
     */
    @PostMapping("/retrieve")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> retrieveBooking(
            @Valid @RequestBody RetrieveBookingRequestDTO request) {

        BookingResponseDTO booking = bookingService.getBooking(
                request.getBookingReference(), request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Booking retrieved successfully", booking));
    }

    /**
     * Cancel an active booking and immediately release reserved availability.
     * POST /api/bookings/{reference}/cancel
     */
    @PostMapping("/{reference}/cancel")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> cancelBooking(
            @PathVariable String reference,
            @RequestBody(required = false) CancelBookingRequestDTO body,
            @RequestParam(required = false) String email) {

        String guestEmail = (body != null && body.getEmail() != null) ? body.getEmail() : email;

        BookingResponseDTO cancelledBooking = bookingService.cancelBooking(reference, guestEmail);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully. Reserved room has been released.", cancelledBooking));
    }
}
