package com.example.car_rental_sys.dao;

import com.example.car_rental_sys.model.Payment;
import com.example.car_rental_sys.model.PaymentStatus;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object contract for {@link Payment} transactions.
 */
public interface PaymentDAO extends GenericDAO<Payment, Integer> {

    /**
     * Finds the payment transaction associated with an order.
     *
     * @param orderId order ID
     * @return an {@link Optional} containing the payment if recorded
     */
    Optional<Payment> findByOrderId(int orderId) throws DAOException;

    /**
     * Finds a payment by its unique transaction reference code.
     *
     * @param reference reference identifier
     * @return an {@link Optional} containing the payment if found
     */
    Optional<Payment> findByTransactionReference(String reference) throws DAOException;

    /**
     * Retrieves all payments by status (PENDING, SUCCESS, FAILED, REFUNDED).
     *
     * @param status payment status
     * @return list of payments
     */
    List<Payment> findByStatus(PaymentStatus status) throws DAOException;

    /**
     * Updates payment status.
     *
     * @param paymentId payment ID
     * @param status    new status
     * @return true if updated successfully
     */
    boolean updateStatus(int paymentId, PaymentStatus status) throws DAOException;
}
