package com.hotelbooking.portal.exception;

/**
 * Exception thrown when no rooms are available for the requested hotel and dates.
 */
public class RoomUnavailableException extends RuntimeException {

    public RoomUnavailableException(String message) {
        super(message);
    }
}
