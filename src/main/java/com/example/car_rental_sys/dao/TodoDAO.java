package com.example.car_rental_sys.dao;

import com.example.car_rental_sys.model.Todo;

import java.util.List;

/**
 * Data Access Object contract for operational checklist {@link Todo} tasks.
 */
public interface TodoDAO extends GenericDAO<Todo, Integer> {

    /**
     * Retrieves all todo items assigned to a user.
     *
     * @param userId user ID
     * @return list of todos
     */
    List<Todo> findByUserId(int userId) throws DAOException;

    /**
     * Retrieves uncompleted tasks for a user.
     *
     * @param userId user ID
     * @return list of pending tasks
     */
    List<Todo> findPendingByUserId(int userId) throws DAOException;

    /**
     * Toggles the completion state of a task.
     *
     * @param todoId      task ID
     * @param isCompleted completion state
     * @return true if updated
     */
    boolean markAsCompleted(int todoId, boolean isCompleted) throws DAOException;
}
