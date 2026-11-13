package com.hotelbooking.portal;

import com.hotelbooking.portal.entity.Hotel;
import com.hotelbooking.portal.repository.BookingRepository;
import com.hotelbooking.portal.repository.HotelRepository;
import com.hotelbooking.portal.repository.RoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verification tests to ensure entities, repositories, and sample data load properly.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class HotelBookingApplicationTests {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    @DisplayName("Context loads and repositories are successfully injected")
    void contextLoads() {
        assertNotNull(hotelRepository, "HotelRepository should be injected");
        assertNotNull(roomRepository, "RoomRepository should be injected");
        assertNotNull(bookingRepository, "BookingRepository should be injected");
    }

    @Test
    @DisplayName("DataInitializer should seed at least 5 sample hotels with rooms")
    void shouldSeedSampleHotels() {
        List<Hotel> hotels = hotelRepository.findAll();
        assertTrue(hotels.size() >= 5, "Database should contain at least 5 sample hotels");

        // Verify that rooms are properly associated with hotels
        Hotel firstHotel = hotels.get(0);
        assertNotNull(firstHotel.getName());
        assertNotNull(firstHotel.getLocation());
        assertNotNull(firstHotel.getPricePerNight());
        assertTrue(firstHotel.getTotalRooms() > 0);
        assertFalse(firstHotel.getRooms().isEmpty(), "Hotel should have associated physical rooms");
    }

    @Test
    @DisplayName("Hotel search by location should be case-insensitive")
    void shouldSearchHotelsByLocation() {
        List<Hotel> nyHotels = hotelRepository.findByLocationContainingIgnoreCase("new york");
        assertFalse(nyHotels.isEmpty(), "Should find hotel in New York");
        assertEquals("The Grand Heritage Hotel", nyHotels.get(0).getName());
    }
}
