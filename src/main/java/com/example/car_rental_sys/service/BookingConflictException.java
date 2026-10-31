package com.example.car_rental_sys.service;

/**
 * Exception thrown when a vehicle reservation date conflicts with an existing booking.
 */
public class BookingConflictException extends ServiceException {

    private static final long serialVersionUID = 1L;

    public BookingConflictException(String message) {
        super(message);
    }
}
