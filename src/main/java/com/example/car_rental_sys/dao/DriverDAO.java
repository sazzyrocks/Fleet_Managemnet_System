package com.example.car_rental_sys.dao;

import com.example.car_rental_sys.model.Driver;
import com.example.car_rental_sys.model.DriverStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object contract for fleet {@link Driver} personnel.
 */
public interface DriverDAO extends GenericDAO<Driver, Integer> {

    /**
     * Finds a driver profile linked to a specific user account.
     *
     * @param userId user ID
     * @return an {@link Optional} containing the driver if exists
     */
    Optional<Driver> findByUserId(int userId) throws DAOException;

    /**
     * Finds a driver by their official driving license number.
     *
     * @param licenseNumber license number string
     * @return an {@link Optional} containing the driver if exists
     */
    Optional<Driver> findByLicenseNumber(String licenseNumber) throws DAOException;

    /**
     * Retrieves all drivers currently available for assignment.
     *
     * @return list of available drivers
     */
    List<Driver> findAvailableDrivers() throws DAOException;

    /**
     * Updates driver availability status.
     *
     * @param driverId driver ID
     * @param status   new status
     * @return true if updated successfully
     */
    boolean updateStatus(int driverId, DriverStatus status) throws DAOException;

    /**
     * Updates the driver's aggregate rating based on customer reviews.
     *
     * @param driverId driver ID
     * @param rating   new calculated average rating
     * @return true if updated successfully
     */
    boolean updateRating(int driverId, BigDecimal rating) throws DAOException;
}
