package com.hotelbooking.portal.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Data Transfer Object for hotel search and browsing representations.
 */
public class HotelSummaryDTO {

    private Long id;
    private String name;
    private String location;
    private String description;
    private List<String> amenities = new ArrayList<>();
    private BigDecimal pricePerNight;
    private Integer totalRooms;
    private Integer availableRooms;
    private String imageUrl;
    private Double rating;

    public HotelSummaryDTO() {
    }

    public HotelSummaryDTO(Long id, String name, String location, String description,
                           String amenitiesString, BigDecimal pricePerNight,
                           Integer totalRooms, Integer availableRooms,
                           String imageUrl, Double rating) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        if (amenitiesString != null && !amenitiesString.trim().isEmpty()) {
            this.amenities = Arrays.stream(amenitiesString.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }
        this.pricePerNight = pricePerNight;
        this.totalRooms = totalRooms;
        this.availableRooms = availableRooms;
        this.imageUrl = imageUrl;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public Integer getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(Integer totalRooms) {
        this.totalRooms = totalRooms;
    }

    public Integer getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(Integer availableRooms) {
        this.availableRooms = availableRooms;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
