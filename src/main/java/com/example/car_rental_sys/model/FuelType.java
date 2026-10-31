package com.example.car_rental_sys.model;

/**
 * Vehicle fuel and powertrain types.
 */
public enum FuelType {
    PETROL,
    DIESEL,
    ELECTRIC,
    HYBRID;

    public static FuelType fromString(String value) {
        if (value == null) return PETROL;
        try {
            return FuelType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return PETROL;
        }
    }
}
