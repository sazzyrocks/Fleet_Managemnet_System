package com.example.car_rental_sys.model;

/**
 * System user roles.
 */
public enum Role {
    ADMIN,
    CUSTOMER,
    DRIVER;

    public static Role fromString(String value) {
        if (value == null) return CUSTOMER;
        try {
            return Role.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return CUSTOMER;
        }
    }
}
