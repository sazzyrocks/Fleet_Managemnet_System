package com.example.car_rental_sys;

/**
 * Main Launcher bootstrap for packaging and fat JAR execution.
 *
 * <p>Bypasses the JavaFX runtime component check when launching without a modular module-path.</p>
 */
public final class Launcher {

    private Launcher() {
        // Prevent instantiation
    }

    public static void main(String[] args) {
        Application.main(args);
    }
}

