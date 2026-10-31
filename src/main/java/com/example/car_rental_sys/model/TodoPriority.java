package com.example.car_rental_sys.model;

/**
 * Task and todo priority levels.
 */
public enum TodoPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    public static TodoPriority fromString(String value) {
        if (value == null) return MEDIUM;
        try {
            return TodoPriority.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return MEDIUM;
        }
    }
}
