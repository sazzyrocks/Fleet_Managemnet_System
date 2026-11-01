package com.example.car_rental_sys.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests verifying the DAOFactory singleton and complete DAO exposure.
 */
class DAOFactoryTest {

    @Test
    @DisplayName("DAOFactory should return consistent singleton instance")
    void shouldReturnSingletonInstance() {
        DAOFactory factory1 = DAOFactory.getInstance();
        DAOFactory factory2 = DAOFactory.getInstance();

        assertNotNull(factory1);
        assertSame(factory1, factory2, "DAOFactory must be a strict singleton");
    }

    @Test
    @DisplayName("DAOFactory should expose non-null implementations for all 12 domain DAOs")
    void shouldExposeAllDomainDAOs() {
        DAOFactory factory = DAOFactory.getInstance();

        assertNotNull(factory.getUserDAO(), "UserDAO must be initialized");
        assertNotNull(factory.getVehicleDAO(), "VehicleDAO must be initialized");
        assertNotNull(factory.getVehicleCategoryDAO(), "VehicleCategoryDAO must be initialized");
        assertNotNull(factory.getOrderDAO(), "OrderDAO must be initialized");
        assertNotNull(factory.getPaymentDAO(), "PaymentDAO must be initialized");
        assertNotNull(factory.getBankCardDAO(), "BankCardDAO must be initialized");
        assertNotNull(factory.getDriverDAO(), "DriverDAO must be initialized");
        assertNotNull(factory.getVehicleDiagnosticDAO(), "VehicleDiagnosticDAO must be initialized");
        assertNotNull(factory.getReviewDAO(), "ReviewDAO must be initialized");
        assertNotNull(factory.getSystemLogDAO(), "SystemLogDAO must be initialized");
        assertNotNull(factory.getMessageDAO(), "MessageDAO must be initialized");
        assertNotNull(factory.getTodoDAO(), "TodoDAO must be initialized");
    }
}
