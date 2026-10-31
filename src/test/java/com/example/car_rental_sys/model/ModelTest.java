package com.example.car_rental_sys.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests verifying domain entity encapsulation, enum mappings, and business calculations.
 */
class ModelTest {

    @Test
    @DisplayName("User entity should correctly report role and status helpers")
    void shouldValidateUserRoleAndStatusHelpers() {
        User admin = new User();
        admin.setRole(Role.ADMIN);
        admin.setStatus(UserStatus.ACTIVE);

        assertTrue(admin.isAdmin());
        assertFalse(admin.isCustomer());
        assertFalse(admin.isDriver());
        assertTrue(admin.isActive());
    }

    @Test
    @DisplayName("Vehicle entity should format display name and availability properly")
    void shouldValidateVehicleProperties() {
        Vehicle vehicle = new Vehicle();
        vehicle.setBrand("Tesla");
        vehicle.setModel("Model 3");
        vehicle.setYear(2023);
        vehicle.setDailyRate(new BigDecimal("120.00"));
        vehicle.setStatus(VehicleStatus.AVAILABLE);

        assertEquals("Tesla Model 3 (2023)", vehicle.getDisplayName());
        assertTrue(vehicle.isAvailable());
    }

    @Test
    @DisplayName("BankCard masked number should protect sensitive card details")
    void shouldMaskBankCardNumberProperly() {
        BankCard card = new BankCard();
        card.setCardNumber("4111222233334444");

        assertEquals("**** **** **** 4444", card.getMaskedCardNumber());
    }

    @Test
    @DisplayName("Order rental days should calculate duration accurately")
    void shouldCalculateRentalDays() {
        Order order = new Order();
        LocalDateTime start = LocalDateTime.of(2026, 11, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 11, 5, 10, 0);
        order.setStartDate(start);
        order.setEndDate(end);

        assertEquals(4, order.getRentalDays());
    }

    @Test
    @DisplayName("VehicleDiagnostic alerts should flag overheating and low fuel")
    void shouldFlagDiagnosticAlerts() {
        VehicleDiagnostic normal = new VehicleDiagnostic();
        normal.setEngineTemperature(new BigDecimal("92.0"));
        normal.setFuelLevelPercent(65);

        assertFalse(normal.isEngineOverheating());
        assertFalse(normal.isLowFuel());

        VehicleDiagnostic critical = new VehicleDiagnostic();
        critical.setEngineTemperature(new BigDecimal("112.5"));
        critical.setFuelLevelPercent(8);

        assertTrue(critical.isEngineOverheating());
        assertTrue(critical.isLowFuel());
    }
}
