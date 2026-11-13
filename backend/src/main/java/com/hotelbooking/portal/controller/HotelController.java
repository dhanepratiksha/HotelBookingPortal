package com.hotelbooking.portal.controller;

import com.hotelbooking.portal.dto.ApiResponse;
import com.hotelbooking.portal.dto.HotelSummaryDTO;
import com.hotelbooking.portal.dto.PriceCalculationDTO;
import com.hotelbooking.portal.dto.SmartStayRecommendationDTO;
import com.hotelbooking.portal.service.BookingService;
import com.hotelbooking.portal.service.HotelService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for hotel browsing, search, and availability inspection.
 */
@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;
    private final BookingService bookingService;

    public HotelController(HotelService hotelService, BookingService bookingService) {
        this.hotelService = hotelService;
        this.bookingService = bookingService;
    }

    /**
     * Browse all hotels or search by location and date range.
     * GET /api/hotels?location=Miami&checkIn=2026-11-20&checkOut=2026-11-23
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<HotelSummaryDTO>>> searchHotels(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {

        List<HotelSummaryDTO> hotels = hotelService.searchHotels(location, checkIn, checkOut);
        return ResponseEntity.ok(ApiResponse.success("Hotels retrieved successfully", hotels));
    }

    /**
     * Get details for a specific hotel by ID with availability calculated for requested dates.
     * GET /api/hotels/{id}?checkIn=2026-11-20&checkOut=2026-11-23
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HotelSummaryDTO>> getHotelDetails(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {

        HotelSummaryDTO hotel = hotelService.getHotelById(id, checkIn, checkOut);
        return ResponseEntity.ok(ApiResponse.success("Hotel details retrieved successfully", hotel));
    }

    /**
     * Get real-time room availability count for a specific hotel and date range.
     * GET /api/hotels/{id}/availability?checkIn=2026-11-20&checkOut=2026-11-23
     */
    @GetMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkAvailability(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {

        int availableRooms = hotelService.calculateAvailableRooms(id, checkIn, checkOut);
        HotelSummaryDTO hotel = hotelService.getHotelById(id, null, null);

        Map<String, Object> data = new HashMap<>();
        data.put("hotelId", id);
        data.put("hotelName", hotel.getName());
        data.put("checkInDate", checkIn);
        data.put("checkOutDate", checkOut);
        data.put("totalRooms", hotel.getTotalRooms());
        data.put("availableRooms", availableRooms);
        data.put("isAvailable", availableRooms > 0);

        return ResponseEntity.ok(ApiResponse.success("Availability checked successfully", data));
    }

    /**
     * Pre-booking price quote calculated strictly on the backend.
     * GET /api/hotels/{id}/calculate-price?checkIn=2026-11-20&checkOut=2026-11-23
     */
    @GetMapping("/{id}/calculate-price")
    public ResponseEntity<ApiResponse<PriceCalculationDTO>> calculatePrice(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {

        PriceCalculationDTO priceQuote = bookingService.calculatePriceAndAvailability(id, checkIn, checkOut);
        return ResponseEntity.ok(ApiResponse.success("Price calculated successfully", priceQuote));
    }

    /**
     * Surprise Feature: Smart Stay Recommendation
     * Returns the best-value available hotel based on price, amenities count, and availability.
     * GET /api/hotels/smart-recommendation?location=Miami&checkIn=2026-11-20&checkOut=2026-11-23
     */
    @GetMapping("/smart-recommendation")
    public ResponseEntity<ApiResponse<SmartStayRecommendationDTO>> getSmartStayRecommendation(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {

        SmartStayRecommendationDTO recommendation = hotelService.getSmartStayRecommendation(location, checkIn, checkOut);
        if (recommendation == null) {
            return ResponseEntity.ok(ApiResponse.success("No hotels with availability found for the criteria", null));
        }
        return ResponseEntity.ok(ApiResponse.success("Smart Stay recommendation retrieved successfully", recommendation));
    }
}
