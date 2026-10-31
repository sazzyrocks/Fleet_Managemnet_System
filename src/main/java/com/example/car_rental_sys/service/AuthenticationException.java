package com.example.car_rental_sys.service;

/**
 * Exception thrown when user authentication or authorization fails.
 */
public class AuthenticationException extends ServiceException {

    private static final long serialVersionUID = 1L;

    public AuthenticationException(String message) {
        super(message);
    }
}
