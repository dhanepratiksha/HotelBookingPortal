package com.hotelbooking.portal.service;

import com.hotelbooking.portal.dto.HotelSummaryDTO;
import com.hotelbooking.portal.dto.SmartStayRecommendationDTO;
import com.hotelbooking.portal.entity.BookingStatus;
import com.hotelbooking.portal.entity.Hotel;
import com.hotelbooking.portal.exception.InvalidBookingDateException;
import com.hotelbooking.portal.exception.ResourceNotFoundException;
import com.hotelbooking.portal.repository.BookingRepository;
import com.hotelbooking.portal.repository.HotelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service managing hotel browsing, search by location, and date-based availability calculations.
 */
@Service
@Transactional(readOnly = true)
public class HotelService {

    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;

    public HotelService(HotelRepository hotelRepository, BookingRepository bookingRepository) {
        this.hotelRepository = hotelRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Retrieve all hotels with general availability (total room capacity).
     */
    public List<HotelSummaryDTO> getAllHotels() {
        return hotelRepository.findAll().stream()
                .map(this::mapToSummaryDTOWithoutDates)
                .toList();
    }

    /**
     * Search hotels by location and calculate available rooms for the requested date window.
     *
     * @param location     Optional location search term (e.g. "Miami", "Paris")
     * @param checkInDate  Optional check-in date
     * @param checkOutDate Optional check-out date
     * @return List of matching hotels with real-time available room count
     */
    public List<HotelSummaryDTO> searchHotels(String location, LocalDate checkInDate, LocalDate checkOutDate) {
        // Validate dates if either date is provided
        if (checkInDate != null || checkOutDate != null) {
            validateDates(checkInDate, checkOutDate);
        }

        List<Hotel> matchedHotels;
        if (location != null && !location.trim().isEmpty()) {
            matchedHotels = hotelRepository.findByLocationContainingIgnoreCase(location.trim());
        } else {
            matchedHotels = hotelRepository.findAll();
        }

        return matchedHotels.stream()
                .map(hotel -> mapToSummaryDTO(hotel, checkInDate, checkOutDate))
                .toList();
    }

    /**
     * Retrieve hotel details by ID with available rooms calculated for requested dates.
     */
    public HotelSummaryDTO getHotelById(Long id, LocalDate checkInDate, LocalDate checkOutDate) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + id));

        if (checkInDate != null || checkOutDate != null) {
            validateDates(checkInDate, checkOutDate);
        }

        return mapToSummaryDTO(hotel, checkInDate, checkOutDate);
    }

    /**
     * Calculate how many rooms remain unreserved for a specific hotel and date window.
     */
    public int calculateAvailableRooms(Long hotelId, LocalDate checkInDate, LocalDate checkOutDate) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId));

        if (checkInDate == null || checkOutDate == null) {
            return hotel.getTotalRooms();
        }

        validateDates(checkInDate, checkOutDate);

        long activeBookings = bookingRepository.countActiveOverlappingBookings(
                hotelId, checkInDate, checkOutDate, BookingStatus.CONFIRMED);

        return Math.max(0, hotel.getTotalRooms() - (int) activeBookings);
    }

    /**
     * Surprise Feature: "Smart Stay Recommendation"
     * Recommends the highest-value hotel based on:
     * 1. Room price per night (affordability)
     * 2. Number of amenities offered (richness of stay)
     * 3. Real-time availability for the requested dates
     *
     * Value Formula: Value Score = ((Amenities Count * 15.0) + (Rating * 10.0)) / Price * 100
     */
    public SmartStayRecommendationDTO getSmartStayRecommendation(String location, LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate != null || checkOutDate != null) {
            validateDates(checkInDate, checkOutDate);
        }

        List<Hotel> candidates;
        if (location != null && !location.trim().isEmpty()) {
            candidates = hotelRepository.findByLocationContainingIgnoreCase(location.trim());
        } else {
            candidates = hotelRepository.findAll();
        }

        if (candidates.isEmpty()) {
            return null;
        }

        Hotel bestHotel = null;
        double bestScore = -1.0;
        int bestAvailableRooms = 0;
        int bestAmenitiesCount = 0;

        for (Hotel hotel : candidates) {
            int availableRooms = calculateAvailableRooms(hotel.getId(), checkInDate, checkOutDate);
            // Must have rooms available to be recommended
            if (availableRooms <= 0) {
                continue;
            }

            int amenitiesCount = 0;
            if (hotel.getAmenities() != null && !hotel.getAmenities().trim().isEmpty()) {
                amenitiesCount = hotel.getAmenities().split(",").length;
            }

            double price = hotel.getPricePerNight().doubleValue();
            if (price <= 0) {
                price = 100.0;
            }

            double rating = (hotel.getRating() != null) ? hotel.getRating() : 4.0;

            // Transparent value formula: perks and quality relative to price
            double valueScore = Math.round((((amenitiesCount * 15.0) + (rating * 10.0)) / price) * 100.0 * 10.0) / 10.0;

            if (valueScore > bestScore) {
                bestScore = valueScore;
                bestHotel = hotel;
                bestAvailableRooms = availableRooms;
                bestAmenitiesCount = amenitiesCount;
            }
        }

        if (bestHotel == null) {
            return null;
        }

        String reason = String.format("Best value stay: Offers %d amenities and a %.1f-star rating at only $%.2f/night with %d room(s) available.",
                bestAmenitiesCount,
                (bestHotel.getRating() != null ? bestHotel.getRating() : 4.5),
                bestHotel.getPricePerNight(),
                bestAvailableRooms);

        HotelSummaryDTO summaryDTO = mapToSummaryDTO(bestHotel, checkInDate, checkOutDate);

        return new SmartStayRecommendationDTO(
                summaryDTO,
                bestScore,
                "Smart Stay: Best Value Pick",
                reason,
                bestAvailableRooms,
                bestAmenitiesCount
        );
    }

    /**
     * Validates that check-in is not in the past and check-out is strictly after check-in.
     */
    public void validateDates(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate == null || checkOutDate == null) {
            throw new InvalidBookingDateException("Both check-in date and check-out date must be provided.");
        }
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new InvalidBookingDateException("Check-in date cannot be in the past (" + checkInDate + ").");
        }
        if (!checkOutDate.isAfter(checkInDate)) {
            throw new InvalidBookingDateException("Check-out date (" + checkOutDate + ") must be after check-in date (" + checkInDate + ").");
        }
    }

    private HotelSummaryDTO mapToSummaryDTOWithoutDates(Hotel hotel) {
        return new HotelSummaryDTO(
                hotel.getId(),
                hotel.getName(),
                hotel.getLocation(),
                hotel.getDescription(),
                hotel.getAmenities(),
                hotel.getPricePerNight(),
                hotel.getTotalRooms(),
                hotel.getTotalRooms(), // Default available rooms equals total when no dates chosen
                hotel.getImageUrl(),
                hotel.getRating()
        );
    }

    private HotelSummaryDTO mapToSummaryDTO(Hotel hotel, LocalDate checkInDate, LocalDate checkOutDate) {
        int availableRooms = hotel.getTotalRooms();
        if (checkInDate != null && checkOutDate != null) {
            long activeBookings = bookingRepository.countActiveOverlappingBookings(
                    hotel.getId(), checkInDate, checkOutDate, BookingStatus.CONFIRMED);
            availableRooms = Math.max(0, hotel.getTotalRooms() - (int) activeBookings);
        }

        return new HotelSummaryDTO(
                hotel.getId(),
                hotel.getName(),
                hotel.getLocation(),
                hotel.getDescription(),
                hotel.getAmenities(),
                hotel.getPricePerNight(),
                hotel.getTotalRooms(),
                availableRooms,
                hotel.getImageUrl(),
                hotel.getRating()
        );
    }
}
