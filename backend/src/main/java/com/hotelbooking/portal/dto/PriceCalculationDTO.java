package com.hotelbooking.portal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO returned by the price calculation pre-booking endpoint.
 */
public class PriceCalculationDTO {

    private Long hotelId;
    private String hotelName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Long numberOfNights;
    private BigDecimal roomPricePerNight;
    private BigDecimal totalAmount;
    private Integer availableRooms;

    public PriceCalculationDTO() {
    }

    public PriceCalculationDTO(Long hotelId, String hotelName, LocalDate checkInDate,
                               LocalDate checkOutDate, Long numberOfNights,
                               BigDecimal roomPricePerNight, BigDecimal totalAmount,
                               Integer availableRooms) {
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfNights = numberOfNights;
        this.roomPricePerNight = roomPricePerNight;
        this.totalAmount = totalAmount;
        this.availableRooms = availableRooms;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public Long getNumberOfNights() {
        return numberOfNights;
    }

    public void setNumberOfNights(Long numberOfNights) {
        this.numberOfNights = numberOfNights;
    }

    public BigDecimal getRoomPricePerNight() {
        return roomPricePerNight;
    }

    public void setRoomPricePerNight(BigDecimal roomPricePerNight) {
        this.roomPricePerNight = roomPricePerNight;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(Integer availableRooms) {
        this.availableRooms = availableRooms;
    }
}
