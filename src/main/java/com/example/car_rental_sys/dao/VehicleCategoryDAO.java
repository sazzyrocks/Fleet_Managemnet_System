package com.example.car_rental_sys.dao;

import com.example.car_rental_sys.model.VehicleCategory;

import java.util.Optional;

/**
 * Data Access Object contract for {@link VehicleCategory} entities.
 */
public interface VehicleCategoryDAO extends GenericDAO<VehicleCategory, Integer> {

    /**
     * Finds a category by its name (Sedan, SUV, Luxury, Electric).
     *
     * @param name category name
     * @return an {@link Optional} containing the category if found
     */
    Optional<VehicleCategory> findByName(String name) throws DAOException;
}
