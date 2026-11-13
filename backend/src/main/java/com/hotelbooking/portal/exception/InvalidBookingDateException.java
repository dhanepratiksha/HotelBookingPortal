package com.hotelbooking.portal.exception;

/**
 * Exception thrown when requested check-in or check-out dates fail validation rules
 * (e.g. check-in in the past, or check-out before or equal to check-in).
 */
public class InvalidBookingDateException extends RuntimeException {

    public InvalidBookingDateException(String message) {
        super(message);
    }
}
