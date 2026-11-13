package com.hotelbooking.portal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Outgoing response payload representing booking confirmation or retrieved details.
 */
public class BookingResponseDTO {

    private String bookingReference;
    private Long hotelId;
    private String hotelName;
    private String hotelLocation;
    private String guestName;
    private String guestEmail;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Long numberOfNights;
    private BigDecimal roomPricePerNight;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private String assignedRoomNumber;

    public BookingResponseDTO() {
    }

    public BookingResponseDTO(String bookingReference, Long hotelId, String hotelName,
                              String hotelLocation, String guestName, String guestEmail,
                              LocalDate checkInDate, LocalDate checkOutDate,
                              Long numberOfNights, BigDecimal roomPricePerNight,
                              BigDecimal totalAmount, String status,
                              LocalDateTime createdAt, String assignedRoomNumber) {
        this.bookingReference = bookingReference;
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.hotelLocation = hotelLocation;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfNights = numberOfNights;
        this.roomPricePerNight = roomPricePerNight;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.assignedRoomNumber = assignedRoomNumber;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
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

    public String getHotelLocation() {
        return hotelLocation;
    }

    public void setHotelLocation(String hotelLocation) {
        this.hotelLocation = hotelLocation;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getAssignedRoomNumber() {
        return assignedRoomNumber;
    }

    public void setAssignedRoomNumber(String assignedRoomNumber) {
        this.assignedRoomNumber = assignedRoomNumber;
    }
}
