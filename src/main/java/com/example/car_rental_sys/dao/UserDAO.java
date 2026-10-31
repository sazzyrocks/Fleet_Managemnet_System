package com.example.car_rental_sys.dao;

import com.example.car_rental_sys.model.Role;
import com.example.car_rental_sys.model.User;
import com.example.car_rental_sys.model.UserStatus;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object contract for {@link User} entities.
 */
public interface UserDAO extends GenericDAO<User, Integer> {

    /**
     * Finds a user by their unique username.
     *
     * @param username the username to look up
     * @return an {@link Optional} containing the user if found
     */
    Optional<User> findByUsername(String username) throws DAOException;

    /**
     * Finds a user by their unique email address.
     *
     * @param email the email to look up
     * @return an {@link Optional} containing the user if found
     */
    Optional<User> findByEmail(String email) throws DAOException;

    /**
     * Retrieves all users matching a specific role (Admin, Customer, Driver).
     *
     * @param role the target role
     * @return list of matching users
     */
    List<User> findByRole(Role role) throws DAOException;

    /**
     * Retrieves all users matching an account status.
     *
     * @param status the target account status
     * @return list of matching users
     */
    List<User> findByStatus(UserStatus status) throws DAOException;

    /**
     * Updates only the password hash for a specific user.
     *
     * @param userId          the user ID
     * @param newPasswordHash the new BCrypt hashed password
     * @return true if updated successfully
     */
    boolean updatePassword(int userId, String newPasswordHash) throws DAOException;

    /**
     * Updates the status of an account (ACTIVE, SUSPENDED, PENDING).
     *
     * @param userId the user ID
     * @param status new status
     * @return true if updated successfully
     */
    boolean updateStatus(int userId, UserStatus status) throws DAOException;

    /**
     * Checks if a username is already taken.
     *
     * @param username the username to check
     * @return true if exists
     */
    boolean existsByUsername(String username) throws DAOException;

    /**
     * Checks if an email is already registered.
     *
     * @param email the email to check
     * @return true if exists
     */
    boolean existsByEmail(String email) throws DAOException;
}
