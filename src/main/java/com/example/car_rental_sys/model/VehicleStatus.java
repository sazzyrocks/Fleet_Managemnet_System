package com.example.car_rental_sys.model;

/**
 * Operational status of a fleet vehicle.
 */
public enum VehicleStatus {
    AVAILABLE,
    RENTED,
    MAINTENANCE,
    RESERVED;

    public static VehicleStatus fromString(String value) {
        if (value == null) return AVAILABLE;
        try {
            return VehicleStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return AVAILABLE;
        }
    }
}
