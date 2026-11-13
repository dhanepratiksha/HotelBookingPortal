package com.hotelbooking.portal.repository;

import com.hotelbooking.portal.entity.Booking;
import com.hotelbooking.portal.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Booking entity operations.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Retrieve a booking by its unique reference string.
     */
    Optional<Booking> findByBookingReference(String bookingReference);

    /**
     * Retrieve a booking matching both reference and guest email (case-insensitive email).
     */
    Optional<Booking> findByBookingReferenceAndGuestEmailIgnoreCase(String bookingReference, String guestEmail);

    /**
     * Count confirmed overlapping bookings for a hotel during a requested stay window [checkInDate, checkOutDate).
     * Two date ranges overlap if and only if:
     * existing.checkInDate < requestedCheckOutDate AND existing.checkOutDate > requestedCheckInDate
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.hotel.id = :hotelId " +
           "AND b.status = :status " +
           "AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate")
    long countActiveOverlappingBookings(@Param("hotelId") Long hotelId,
                                       @Param("checkInDate") LocalDate checkInDate,
                                       @Param("checkOutDate") LocalDate checkOutDate,
                                       @Param("status") BookingStatus status);

    /**
     * Retrieve IDs of specific rooms that are already booked for overlapping dates.
     */
    @Query("SELECT b.room.id FROM Booking b WHERE b.hotel.id = :hotelId " +
           "AND b.status = :status " +
           "AND b.room IS NOT NULL " +
           "AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate")
    List<Long> findBookedRoomIdsForDates(@Param("hotelId") Long hotelId,
                                        @Param("checkInDate") LocalDate checkInDate,
                                        @Param("checkOutDate") LocalDate checkOutDate,
                                        @Param("status") BookingStatus status);
}
