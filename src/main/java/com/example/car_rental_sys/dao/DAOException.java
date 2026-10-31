package com.example.car_rental_sys.dao;

/**
 * Custom runtime exception representing data access failures.
 *
 * <p>Wraps underlying {@link java.sql.SQLException} instances to decouple
 * service and presentation tiers from relational database implementation details.</p>
 */
public class DAOException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DAOException(String message) {
        super(message);
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}
