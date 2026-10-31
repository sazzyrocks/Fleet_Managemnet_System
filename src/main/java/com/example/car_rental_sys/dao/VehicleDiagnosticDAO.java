package com.example.car_rental_sys.dao;

import com.example.car_rental_sys.model.VehicleDiagnostic;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object contract for {@link VehicleDiagnostic} telemetry data.
 */
public interface VehicleDiagnosticDAO extends GenericDAO<VehicleDiagnostic, Integer> {

    /**
     * Retrieves the most recent telemetry readings recorded for a vehicle.
     *
     * @param vehicleId vehicle ID
     * @return an {@link Optional} containing the latest telemetry
     */
    Optional<VehicleDiagnostic> findLatestByVehicleId(int vehicleId) throws DAOException;

    /**
     * Retrieves historical telemetry records for a vehicle.
     *
     * @param vehicleId vehicle ID
     * @param limit     maximum number of readings
     * @return list of diagnostic records ordered newest to oldest
     */
    List<VehicleDiagnostic> findHistoryByVehicleId(int vehicleId, int limit) throws DAOException;

    /**
     * Retrieves all vehicles currently reporting abnormal engine temperatures (>105°C).
     *
     * @return list of diagnostics in warning condition
     */
    List<VehicleDiagnostic> findOverheatingVehicles() throws DAOException;
}
