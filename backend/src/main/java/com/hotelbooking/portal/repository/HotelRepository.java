package com.hotelbooking.portal.repository;

import com.hotelbooking.portal.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for Hotel entity operations.
 */
@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    /**
     * Search hotels by location keyword (case-insensitive).
     */
    List<Hotel> findByLocationContainingIgnoreCase(String location);

    /**
     * Search hotels by either name or location keyword.
     */
    List<Hotel> findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(String name, String location);
}
