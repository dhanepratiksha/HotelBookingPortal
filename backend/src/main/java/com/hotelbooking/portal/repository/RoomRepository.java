package com.hotelbooking.portal.repository;

import com.hotelbooking.portal.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for Room entity operations.
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * Retrieve all rooms belonging to a specific hotel.
     */
    List<Room> findByHotelId(Long hotelId);

    /**
     * Count total rooms for a hotel.
     */
    long countByHotelId(Long hotelId);
}
