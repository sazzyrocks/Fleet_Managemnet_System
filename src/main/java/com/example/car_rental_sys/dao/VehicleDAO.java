package com.example.car_rental_sys.dao;

import com.example.car_rental_sys.model.Vehicle;
import com.example.car_rental_sys.model.VehicleStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object contract for fleet {@link Vehicle} entities.
 */
public interface VehicleDAO extends GenericDAO<Vehicle, Integer> {

    /**
     * Finds a vehicle by its unique license plate number.
     *
     * @param licensePlate license plate
     * @return an {@link Optional} containing the vehicle if found
     */
    Optional<Vehicle> findByLicensePlate(String licensePlate) throws DAOException;

    /**
     * Retrieves all vehicles matching a given status (AVAILABLE, RENTED, etc.).
     *
     * @param status vehicle status
     * @return list of matching vehicles
     */
    List<Vehicle> findByStatus(VehicleStatus status) throws DAOException;

    /**
     * Retrieves all vehicles belonging to a specific category.
     *
     * @param categoryId category ID
     * @return list of matching vehicles
     */
    List<Vehicle> findByCategoryId(int categoryId) throws DAOException;

    /**
     * Multi-criteria search and filter method for customer catalog browsing.
     *
     * @param keyword    search query matching brand or model (can be null/empty)
     * @param categoryId category ID filter (can be null)
     * @param maxRate    maximum daily rate filter (can be null)
     * @param status     status filter (defaults to AVAILABLE if null)
     * @return list of matching vehicles
     */
    List<Vehicle> searchVehicles(String keyword, Integer categoryId, BigDecimal maxRate, VehicleStatus status) throws DAOException;

    /**
     * Updates the operational status of a vehicle.
     *
     * @param vehicleId vehicle ID
     * @param status    new operational status
     * @return true if updated successfully
     */
    boolean updateStatus(int vehicleId, VehicleStatus status) throws DAOException;

    /**
     * Updates the odometer mileage after a completed rental trip.
     *
     * @param vehicleId  vehicle ID
     * @param newMileage updated total mileage
     * @return true if updated successfully
     */
    boolean updateMileage(int vehicleId, int newMileage) throws DAOException;

    /**
     * Returns count of vehicles filtered by status.
     *
     * @param status vehicle status
     * @return count of vehicles
     */
    long countByStatus(VehicleStatus status) throws DAOException;
}
