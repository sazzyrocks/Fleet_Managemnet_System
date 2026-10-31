package com.example.car_rental_sys.dao;

import com.example.car_rental_sys.model.Review;

import java.util.List;

/**
 * Data Access Object contract for customer {@link Review} feedback.
 */
public interface ReviewDAO extends GenericDAO<Review, Integer> {

    /**
     * Retrieves all reviews submitted for a specific vehicle.
     *
     * @param vehicleId vehicle ID
     * @return list of reviews
     */
    List<Review> findByVehicleId(int vehicleId) throws DAOException;

    /**
     * Retrieves all reviews submitted by a specific user.
     *
     * @param userId user ID
     * @return list of reviews
     */
    List<Review> findByUserId(int userId) throws DAOException;

    /**
     * Computes the average star rating for a vehicle.
     *
     * @param vehicleId vehicle ID
     * @return average rating (0.0 if no reviews exist)
     */
    double getAverageRatingForVehicle(int vehicleId) throws DAOException;
}
