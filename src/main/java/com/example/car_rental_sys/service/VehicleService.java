package com.example.car_rental_sys.service;

import com.example.car_rental_sys.dao.DAOFactory;
import com.example.car_rental_sys.dao.VehicleCategoryDAO;
import com.example.car_rental_sys.dao.VehicleDAO;
import com.example.car_rental_sys.model.Vehicle;
import com.example.car_rental_sys.model.VehicleCategory;
import com.example.car_rental_sys.model.VehicleStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service managing vehicle inventory, catalog search, and status transitions.
 */
public class VehicleService {

    private final VehicleDAO vehicleDAO;
    private final VehicleCategoryDAO categoryDAO;

    public VehicleService() {
        this(DAOFactory.getInstance().getVehicleDAO(), DAOFactory.getInstance().getVehicleCategoryDAO());
    }

    public VehicleService(VehicleDAO vehicleDAO, VehicleCategoryDAO categoryDAO) {
        this.vehicleDAO = vehicleDAO;
        this.categoryDAO = categoryDAO;
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleDAO.findAll();
    }

    public List<Vehicle> getAvailableVehicles() {
        return vehicleDAO.findByStatus(VehicleStatus.AVAILABLE);
    }

    public Optional<Vehicle> getVehicleById(int vehicleId) {
        return vehicleDAO.findById(vehicleId);
    }

    public List<Vehicle> searchVehicles(String keyword, Integer categoryId, BigDecimal maxRate, VehicleStatus status) {
        return vehicleDAO.searchVehicles(keyword, categoryId, maxRate, status);
    }

    public List<VehicleCategory> getAllCategories() {
        return categoryDAO.findAll();
    }

    public Vehicle addVehicle(Vehicle vehicle) {
        if (vehicle.getBrand() == null || vehicle.getBrand().trim().isEmpty()) {
            throw new ServiceException("Vehicle brand is required.");
        }
        if (vehicle.getModel() == null || vehicle.getModel().trim().isEmpty()) {
            throw new ServiceException("Vehicle model is required.");
        }
        if (vehicle.getLicensePlate() == null || vehicle.getLicensePlate().trim().isEmpty()) {
            throw new ServiceException("License plate is required.");
        }
        if (vehicleDAO.findByLicensePlate(vehicle.getLicensePlate().trim()).isPresent()) {
            throw new ServiceException("A vehicle with license plate " + vehicle.getLicensePlate() + " already exists.");
        }
        return vehicleDAO.save(vehicle);
    }

    public boolean updateVehicle(Vehicle vehicle) {
        return vehicleDAO.update(vehicle);
    }

    public boolean updateStatus(int vehicleId, VehicleStatus status) {
        return vehicleDAO.updateStatus(vehicleId, status);
    }

    public boolean deleteVehicle(int vehicleId) {
        return vehicleDAO.deleteById(vehicleId);
    }

    public long getFleetCountByStatus(VehicleStatus status) {
        return vehicleDAO.countByStatus(status);
    }
}
