package com.example.car_rental_sys.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests verifying ServiceFactory singleton and complete service availability.
 */
class ServiceFactoryTest {

    @Test
    @DisplayName("ServiceFactory should enforce singleton design pattern")
    void shouldReturnSingletonInstance() {
        ServiceFactory factory1 = ServiceFactory.getInstance();
        ServiceFactory factory2 = ServiceFactory.getInstance();

        assertNotNull(factory1);
        assertSame(factory1, factory2, "ServiceFactory must return the identical singleton instance");
    }

    @Test
    @DisplayName("ServiceFactory should expose initialized business service components")
    void shouldExposeAllServices() {
        ServiceFactory factory = ServiceFactory.getInstance();

        assertNotNull(factory.getAuthService(), "AuthService must be initialized");
        assertNotNull(factory.getVehicleService(), "VehicleService must be initialized");
        assertNotNull(factory.getRentalService(), "RentalService must be initialized");
        assertNotNull(factory.getPaymentService(), "PaymentService must be initialized");
    }
}
