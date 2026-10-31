package com.example.car_rental_sys.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests verifying the DAO exception handling and generic architecture.
 */
class DAOArchitectureTest {

    @Test
    @DisplayName("DAOException should preserve message and root SQLException cause")
    void shouldPreserveRootCauseInDAOException() {
        SQLException sqlException = new SQLException("Connection timed out", "08001", 1001);
        DAOException daoException = new DAOException("Failed to execute database query", sqlException);

        assertEquals("Failed to execute database query", daoException.getMessage());
        assertNotNull(daoException.getCause());
        assertTrue(daoException.getCause() instanceof SQLException);
        assertEquals("Connection timed out", daoException.getCause().getMessage());
    }

    @Test
    @DisplayName("DAOException should function as unchecked RuntimeException")
    void shouldBeAnUncheckedException() {
        DAOException exception = new DAOException("Custom data access error");
        assertTrue(exception instanceof RuntimeException, "DAOException must extend RuntimeException to avoid checked exception pollution");
    }
}
