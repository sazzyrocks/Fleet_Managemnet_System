package com.example.car_rental_sys.model;

/**
 * System user account status.
 */
public enum UserStatus {
    ACTIVE,
    SUSPENDED,
    PENDING;

    public static UserStatus fromString(String value) {
        if (value == null) return ACTIVE;
        try {
            return UserStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
