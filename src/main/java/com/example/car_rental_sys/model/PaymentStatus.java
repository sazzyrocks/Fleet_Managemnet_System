package com.example.car_rental_sys.model;

/**
 * Payment transaction status.
 */
public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    REFUNDED;

    public static PaymentStatus fromString(String value) {
        if (value == null) return PENDING;
        try {
            return PaymentStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return PENDING;
        }
    }
}
