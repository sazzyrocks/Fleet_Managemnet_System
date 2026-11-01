package com.example.car_rental_sys.dao.impl;

import com.example.car_rental_sys.dao.DAOException;
import com.example.car_rental_sys.dao.MessageDAO;
import com.example.car_rental_sys.db.DatabaseConnection;
import com.example.car_rental_sys.model.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Raw JDBC implementation of {@link MessageDAO}.
 */
public class MessageDAOImpl implements MessageDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private static final String BASE_SELECT =
            "SELECT m.*, su.username AS sender_name, ru.username AS receiver_name " +
            "FROM messages m " +
            "JOIN users su ON m.sender_id = su.user_id " +
            "JOIN users ru ON m.receiver_id = ru.user_id ";

    private Message mapResultSet(ResultSet rs) throws SQLException {
        Message m = new Message();
        m.setMessageId(rs.getInt("message_id"));
        m.setSenderId(rs.getInt("sender_id"));
        m.setReceiverId(rs.getInt("receiver_id"));
        m.setSubject(rs.getString("subject"));
        m.setContent(rs.getString("content"));
        m.setIsRead(rs.getBoolean("is_read"));
        try { m.setSenderName(rs.getString("sender_name")); } catch (SQLException ignored) {}
        try { m.setReceiverName(rs.getString("receiver_name")); } catch (SQLException ignored) {}
        Timestamp sent = rs.getTimestamp("sent_at");
        if (sent != null) m.setSentAt(sent.toLocalDateTime());
        return m;
    }

    @Override
    public Optional<Message> findById(Integer id) throws DAOException {
        String sql = BASE_SELECT + "WHERE m.message_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding message by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Message> findAll() throws DAOException {
        String sql = BASE_SELECT + "ORDER BY m.message_id DESC";
        List<Message> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            throw new DAOException("Error retrieving messages", e);
        }
        return list;
    }

    @Override
    public Message save(Message m) throws DAOException {
        String sql = "INSERT INTO messages (sender_id, receiver_id, subject, content, is_read) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, m.getSenderId());
            stmt.setInt(2, m.getReceiverId());
            stmt.setString(3, m.getSubject());
            stmt.setString(4, m.getContent());
            stmt.setBoolean(5, m.getIsRead() != null ? m.getIsRead() : false);

            stmt.executeUpdate();
            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) m.setMessageId(gk.getInt(1));
            }
            return m;
        } catch (SQLException e) {
            throw new DAOException("Error saving message", e);
        }
    }

    @Override
    public boolean update(Message m) throws DAOException {
        String sql = "UPDATE messages SET sender_id = ?, receiver_id = ?, subject = ?, content = ?, is_read = ? WHERE message_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, m.getSenderId());
            stmt.setInt(2, m.getReceiverId());
            stmt.setString(3, m.getSubject());
            stmt.setString(4, m.getContent());
            stmt.setBoolean(5, m.getIsRead());
            stmt.setInt(6, m.getMessageId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating message ID: " + m.getMessageId(), e);
        }
    }

    @Override
    public boolean deleteById(Integer id) throws DAOException {
        String sql = "DELETE FROM messages WHERE message_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting message ID: " + id, e);
        }
    }

    @Override
    public boolean existsById(Integer id) throws DAOException {
        String sql = "SELECT 1 FROM messages WHERE message_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DAOException("Error checking message existence: " + id, e);
        }
    }

    @Override
    public long count() throws DAOException {
        String sql = "SELECT COUNT(*) FROM messages";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            throw new DAOException("Error counting messages", e);
        }
        return 0;
    }

    @Override
    public List<Message> findByReceiverId(int receiverId) throws DAOException {
        String sql = BASE_SELECT + "WHERE m.receiver_id = ? ORDER BY m.sent_at DESC";
        List<Message> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding messages for user: " + receiverId, e);
        }
        return list;
    }

    @Override
    public List<Message> findUnreadByReceiverId(int receiverId) throws DAOException {
        String sql = BASE_SELECT + "WHERE m.receiver_id = ? AND m.is_read = FALSE ORDER BY m.sent_at DESC";
        List<Message> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding unread messages for user: " + receiverId, e);
        }
        return list;
    }

    @Override
    public boolean markAsRead(int messageId) throws DAOException {
        String sql = "UPDATE messages SET is_read = TRUE WHERE message_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error marking message as read: " + messageId, e);
        }
    }

    @Override
    public long countUnread(int receiverId) throws DAOException {
        String sql = "SELECT COUNT(*) FROM messages WHERE receiver_id = ? AND is_read = FALSE";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Error counting unread messages for user: " + receiverId, e);
        }
        return 0;
    }
}
