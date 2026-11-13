package com.hotelbooking.portal.exception;

/**
 * Exception thrown when a requested entity (Hotel, Booking) is not found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
