package com.hotelbooking.portal.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a guest's room reservation.
 */
@Entity
@Table(name = "bookings", indexes = {
    @Index(name = "idx_booking_ref", columnList = "bookingReference", unique = true),
    @Index(name = "idx_guest_email", columnList = "guestEmail"),
    @Index(name = "idx_dates_hotel", columnList = "hotel_id, checkInDate, checkOutDate, status")
})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String bookingReference;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(nullable = false, length = 100)
    private String guestName;

    @Column(nullable = false, length = 120)
    private String guestEmail;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Column(nullable = false)
    private Long numberOfNights;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal roomPricePerNight;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status = BookingStatus.CONFIRMED;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Booking() {
        this.createdAt = LocalDateTime.now();
    }

    public Booking(String bookingReference, Hotel hotel, Room room, String guestName,
                   String guestEmail, LocalDate checkInDate, LocalDate checkOutDate,
                   Long numberOfNights, BigDecimal roomPricePerNight, BigDecimal totalAmount) {
        this.bookingReference = bookingReference;
        this.hotel = hotel;
        this.room = room;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfNights = numberOfNights;
        this.roomPricePerNight = roomPricePerNight;
        this.totalAmount = totalAmount;
        this.status = BookingStatus.CONFIRMED;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = BookingStatus.CONFIRMED;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
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

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
