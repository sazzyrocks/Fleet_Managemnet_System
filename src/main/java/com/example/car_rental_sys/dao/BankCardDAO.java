package com.example.car_rental_sys.dao;

import com.example.car_rental_sys.model.BankCard;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object contract for customer {@link BankCard} records.
 */
public interface BankCardDAO extends GenericDAO<BankCard, Integer> {

    /**
     * Retrieves all saved payment cards belonging to a customer.
     *
     * @param userId user ID
     * @return list of cards
     */
    List<BankCard> findByUserId(int userId) throws DAOException;

    /**
     * Retrieves the default payment card for a customer.
     *
     * @param userId user ID
     * @return an {@link Optional} containing default card if set
     */
    Optional<BankCard> findDefaultCardByUserId(int userId) throws DAOException;

    /**
     * Updates card wallet balance after debiting for a rental or crediting a refund.
     *
     * @param cardId     card ID
     * @param newBalance updated balance
     * @return true if updated successfully
     */
    boolean updateBalance(int cardId, BigDecimal newBalance) throws DAOException;

    /**
     * Sets a card as the default for a customer and unsets any previous default.
     *
     * @param userId user ID
     * @param cardId card ID to become default
     * @return true if updated successfully
     */
    boolean setDefaultCard(int userId, int cardId) throws DAOException;
}
