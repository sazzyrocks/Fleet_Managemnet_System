package com.example.car_rental_sys.dao;

import com.example.car_rental_sys.model.Order;
import com.example.car_rental_sys.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object contract for rental {@link Order} entities.
 */
public interface OrderDAO extends GenericDAO<Order, Integer> {

    /**
     * Finds an order by its unique tracking order number (e.g. ORD-20261031-1001).
     *
     * @param orderNumber order number string
     * @return an {@link Optional} containing the order if found
     */
    Optional<Order> findByOrderNumber(String orderNumber) throws DAOException;

    /**
     * Retrieves all bookings for a given customer.
     *
     * @param customerId user ID of the customer
     * @return list of orders
     */
    List<Order> findByCustomerId(int customerId) throws DAOException;

    /**
     * Retrieves booking history for a specific vehicle.
     *
     * @param vehicleId vehicle ID
     * @return list of orders
     */
    List<Order> findByVehicleId(int vehicleId) throws DAOException;

    /**
     * Retrieves all orders matching a given status.
     *
     * @param status order status
     * @return list of orders
     */
    List<Order> findByStatus(OrderStatus status) throws DAOException;

    /**
     * Updates the status of an order (CONFIRMED, ACTIVE, COMPLETED, CANCELLED).
     *
     * @param orderId order ID
     * @param status  new status
     * @return true if updated successfully
     */
    boolean updateStatus(int orderId, OrderStatus status) throws DAOException;

    /**
     * Checks whether a vehicle is already reserved for a conflicting time interval.
     * Essential for concurrency and double-booking prevention in rental systems.
     *
     * @param vehicleId      vehicle ID to check
     * @param start          rental start timestamp
     * @param end            rental end timestamp
     * @param excludeOrderId optional order ID to exclude (when editing an existing booking, can be null)
     * @return true if a date overlap conflict exists, false if available
     */
    boolean hasOverlappingRentals(int vehicleId, LocalDateTime start, LocalDateTime end, Integer excludeOrderId) throws DAOException;

    /**
     * Computes total revenue from completed/confirmed orders.
     *
     * @return cumulative revenue sum
     */
    BigDecimal calculateTotalRevenue() throws DAOException;
}
