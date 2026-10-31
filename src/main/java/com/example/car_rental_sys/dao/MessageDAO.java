package com.example.car_rental_sys.dao;

import com.example.car_rental_sys.model.Message;

import java.util.List;

/**
 * Data Access Object contract for user {@link Message} communications.
 */
public interface MessageDAO extends GenericDAO<Message, Integer> {

    /**
     * Retrieves all messages delivered to a recipient.
     *
     * @param receiverId user ID
     * @return list of messages
     */
    List<Message> findByReceiverId(int receiverId) throws DAOException;

    /**
     * Retrieves unread notifications for a user.
     *
     * @param receiverId user ID
     * @return list of unread messages
     */
    List<Message> findUnreadByReceiverId(int receiverId) throws DAOException;

    /**
     * Marks a message as read.
     *
     * @param messageId message ID
     * @return true if updated
     */
    boolean markAsRead(int messageId) throws DAOException;

    /**
     * Counts unread messages for badge counts on navigation bars.
     *
     * @param receiverId user ID
     * @return total count of unread messages
     */
    long countUnread(int receiverId) throws DAOException;
}
