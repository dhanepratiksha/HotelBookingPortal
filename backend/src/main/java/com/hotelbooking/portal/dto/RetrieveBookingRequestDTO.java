package com.hotelbooking.portal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for looking up a booking using reference and guest email.
 */
public class RetrieveBookingRequestDTO {

    @NotBlank(message = "Booking reference is required")
    private String bookingReference;

    @NotBlank(message = "Email address is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    public RetrieveBookingRequestDTO() {
    }

    public RetrieveBookingRequestDTO(String bookingReference, String email) {
        this.bookingReference = bookingReference;
        this.email = email;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
