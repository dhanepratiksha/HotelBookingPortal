package com.hotelbooking.portal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelbooking.portal.dto.BookingRequestDTO;
import com.hotelbooking.portal.dto.CancelBookingRequestDTO;
import com.hotelbooking.portal.entity.Hotel;
import com.hotelbooking.portal.repository.HotelRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end integration tests verifying REST endpoints, HTTP status codes,
 * JSON payloads, and validation error responses.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class HotelBookingApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/hotels should return all seeded hotels with HTTP 200")
    void shouldReturnHotelsList() throws Exception {
        mockMvc.perform(get("/api/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(5))))
                .andExpect(jsonPath("$.data[0].name").isNotEmpty())
                .andExpect(jsonPath("$.data[0].pricePerNight").isNumber());
    }

    @Test
    @DisplayName("GET /api/hotels?location=Paris should filter by location")
    void shouldSearchHotelsByLocation() throws Exception {
        mockMvc.perform(get("/api/hotels").param("location", "Paris"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].location", containsString("Paris")));
    }

    @Test
    @DisplayName("GET /api/hotels/{id}/calculate-price should return backend-calculated total amount")
    void shouldCalculatePriceViaEndpoint() throws Exception {
        Hotel hotel = hotelRepository.findAll().get(0);
        LocalDate checkIn = LocalDate.now().plusDays(2);
        LocalDate checkOut = LocalDate.now().plusDays(5); // 3 nights

        mockMvc.perform(get("/api/hotels/" + hotel.getId() + "/calculate-price")
                        .param("checkIn", checkIn.toString())
                        .param("checkOut", checkOut.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.numberOfNights").value(3))
                .andExpect(jsonPath("$.data.totalAmount").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/bookings should create booking and return HTTP 201 Created")
    void shouldCreateBookingSuccessfully() throws Exception {
        Hotel hotel = hotelRepository.findAll().get(0);
        LocalDate checkIn = LocalDate.now().plusDays(3);
        LocalDate checkOut = LocalDate.now().plusDays(6);

        BookingRequestDTO request = new BookingRequestDTO(
                hotel.getId(),
                "John Doe",
                "john.doe@example.com",
                checkIn,
                checkOut
        );

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.bookingReference").isNotEmpty())
                .andExpect(jsonPath("$.data.guestName").value("John Doe"))
                .andExpect(jsonPath("$.data.guestEmail").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data.numberOfNights").value(3))
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("POST /api/bookings with past check-in date should return HTTP 400 Bad Request")
    void shouldRejectBookingWithPastCheckInDate() throws Exception {
        Hotel hotel = hotelRepository.findAll().get(0);
        LocalDate pastCheckIn = LocalDate.now().minusDays(2);
        LocalDate checkOut = LocalDate.now().plusDays(2);

        BookingRequestDTO request = new BookingRequestDTO(
                hotel.getId(),
                "John Doe",
                "john.doe@example.com",
                pastCheckIn,
                checkOut
        );

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Dates"));
    }

    @Test
    @DisplayName("POST /api/bookings with invalid email should return HTTP 400 Validation Error")
    void shouldRejectBookingWithInvalidEmail() throws Exception {
        Hotel hotel = hotelRepository.findAll().get(0);
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(2);

        BookingRequestDTO request = new BookingRequestDTO(
                hotel.getId(),
                "John Doe",
                "not-an-email",
                checkIn,
                checkOut
        );

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.guestEmail").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/bookings/{reference}/cancel should cancel booking and return HTTP 200")
    void shouldCancelBookingSuccessfully() throws Exception {
        Hotel hotel = hotelRepository.findAll().get(0);
        LocalDate checkIn = LocalDate.now().plusDays(15);
        LocalDate checkOut = LocalDate.now().plusDays(18);

        BookingRequestDTO request = new BookingRequestDTO(
                hotel.getId(),
                "Sarah Connor",
                "sarah@example.com",
                checkIn,
                checkOut
        );

        // 1. Create booking
        String responseContent = mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String reference = objectMapper.readTree(responseContent)
                .path("data").path("bookingReference").asText();

        // 2. Cancel booking
        CancelBookingRequestDTO cancelRequest = new CancelBookingRequestDTO("sarah@example.com");

        mockMvc.perform(post("/api/bookings/" + reference + "/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
    }

    @Test
    @DisplayName("GET /api/hotels/smart-recommendation should return best-value available hotel")
    void shouldReturnSmartStayRecommendation() throws Exception {
        mockMvc.perform(get("/api/hotels/smart-recommendation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.hotel").isNotEmpty())
                .andExpect(jsonPath("$.data.valueScore").isNumber())
                .andExpect(jsonPath("$.data.reason").isNotEmpty())
                .andExpect(jsonPath("$.data.availableRooms", greaterThan(0)))
                .andExpect(jsonPath("$.data.amenitiesCount", greaterThan(0)));
    }
}
