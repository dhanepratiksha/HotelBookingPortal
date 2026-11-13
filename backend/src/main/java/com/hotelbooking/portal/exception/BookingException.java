package com.hotelbooking.portal.exception;

/**
 * Exception thrown for booking-related business constraint violations
 * (e.g. attempting to cancel an already cancelled booking, or email mismatch).
 */
public class BookingException extends RuntimeException {

    public BookingException(String message) {
        super(message);
    }
}
