package com.hotelbooking.portal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for cancelling a booking with reference and verified guest email.
 */
public class CancelBookingRequestDTO {

    @NotBlank(message = "Email address is required to cancel a booking")
    @Email(message = "Please provide a valid email address")
    private String email;

    public CancelBookingRequestDTO() {
    }

    public CancelBookingRequestDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
