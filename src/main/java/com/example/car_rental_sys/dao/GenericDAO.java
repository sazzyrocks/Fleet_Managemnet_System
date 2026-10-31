package com.example.car_rental_sys.dao;

import java.util.List;
import java.util.Optional;

/**
 * Generic Data Access Object interface defining the standard CRUD contract.
 *
 * @param <T>  Domain entity type
 * @param <ID> Primary key identifier type
 */
public interface GenericDAO<T, ID> {

    /**
     * Retrieves an entity by its unique primary key.
     *
     * @param id the primary key
     * @return an {@link Optional} containing the entity if found, or empty if not
     * @throws DAOException on database access error
     */
    Optional<T> findById(ID id) throws DAOException;

    /**
     * Retrieves all entities in the table.
     *
     * @return a list of all entities (empty list if none exist)
     * @throws DAOException on database access error
     */
    List<T> findAll() throws DAOException;

    /**
     * Persists a new entity into the database.
     *
     * @param entity the entity to create
     * @return the saved entity (with generated primary key populated)
     * @throws DAOException on database access error
     */
    T save(T entity) throws DAOException;

    /**
     * Updates an existing entity in the database.
     *
     * @param entity the entity containing updated fields
     * @return true if updated successfully, false if no record was found
     * @throws DAOException on database access error
     */
    boolean update(T entity) throws DAOException;

    /**
     * Deletes an entity by its unique primary key.
     *
     * @param id the primary key
     * @return true if deleted, false if entity was not found
     * @throws DAOException on database access error
     */
    boolean deleteById(ID id) throws DAOException;

    /**
     * Checks if an entity exists by its primary key.
     *
     * @param id the primary key
     * @return true if exists, false otherwise
     * @throws DAOException on database access error
     */
    boolean existsById(ID id) throws DAOException;

    /**
     * Returns the total count of entities in the database table.
     *
     * @return total record count
     * @throws DAOException on database access error
     */
    long count() throws DAOException;
}
