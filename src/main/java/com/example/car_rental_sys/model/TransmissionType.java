package com.example.car_rental_sys.model;

/**
 * Vehicle transmission types.
 */
public enum TransmissionType {
    AUTOMATIC,
    MANUAL;

    public static TransmissionType fromString(String value) {
        if (value == null) return AUTOMATIC;
        try {
            return TransmissionType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return AUTOMATIC;
        }
    }
}
