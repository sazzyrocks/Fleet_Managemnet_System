package com.example.car_rental_sys.model;

/**
 * Driver availability status.
 */
public enum DriverStatus {
    AVAILABLE,
    BUSY,
    OFF_DUTY;

    public static DriverStatus fromString(String value) {
        if (value == null) return AVAILABLE;
        try {
            return DriverStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return AVAILABLE;
        }
    }
}
