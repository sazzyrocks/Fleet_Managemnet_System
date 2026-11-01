package com.example.car_rental_sys.dao.impl;

import com.example.car_rental_sys.dao.DAOException;
import com.example.car_rental_sys.dao.SystemLogDAO;
import com.example.car_rental_sys.db.DatabaseConnection;
import com.example.car_rental_sys.model.SystemLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Raw JDBC implementation of {@link SystemLogDAO}.
 */
public class SystemLogDAOImpl implements SystemLogDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private static final String BASE_SELECT =
            "SELECT l.*, u.username AS user_name " +
            "FROM system_logs l " +
            "LEFT JOIN users u ON l.user_id = u.user_id ";

    private SystemLog mapResultSet(ResultSet rs) throws SQLException {
        SystemLog l = new SystemLog();
        l.setLogId(rs.getInt("log_id"));
        int userId = rs.getInt("user_id");
        if (!rs.wasNull()) l.setUserId(userId);
        l.setActionType(rs.getString("action_type"));
        l.setDetails(rs.getString("details"));
        l.setIpAddress(rs.getString("ip_address"));
        try { l.setUserName(rs.getString("user_name")); } catch (SQLException ignored) {}
        Timestamp ts = rs.getTimestamp("timestamp");
        if (ts != null) l.setTimestamp(ts.toLocalDateTime());
        return l;
    }

    @Override
    public Optional<SystemLog> findById(Integer id) throws DAOException {
        String sql = BASE_SELECT + "WHERE l.log_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding system log by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<SystemLog> findAll() throws DAOException {
        String sql = BASE_SELECT + "ORDER BY l.log_id DESC";
        List<SystemLog> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            throw new DAOException("Error retrieving system logs", e);
        }
        return list;
    }

    @Override
    public SystemLog save(SystemLog log) throws DAOException {
        String sql = "INSERT INTO system_logs (user_id, action_type, details, ip_address) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (log.getUserId() != null) stmt.setInt(1, log.getUserId());
            else stmt.setNull(1, Types.INTEGER);
            stmt.setString(2, log.getActionType());
            stmt.setString(3, log.getDetails());
            stmt.setString(4, log.getIpAddress());

            stmt.executeUpdate();
            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) log.setLogId(gk.getInt(1));
            }
            return log;
        } catch (SQLException e) {
            throw new DAOException("Error recording audit log: " + log.getActionType(), e);
        }
    }

    @Override
    public boolean update(SystemLog log) throws DAOException {
        String sql = "UPDATE system_logs SET user_id = ?, action_type = ?, details = ?, ip_address = ? WHERE log_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (log.getUserId() != null) stmt.setInt(1, log.getUserId());
            else stmt.setNull(1, Types.INTEGER);
            stmt.setString(2, log.getActionType());
            stmt.setString(3, log.getDetails());
            stmt.setString(4, log.getIpAddress());
            stmt.setInt(5, log.getLogId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating system log ID: " + log.getLogId(), e);
        }
    }

    @Override
    public boolean deleteById(Integer id) throws DAOException {
        String sql = "DELETE FROM system_logs WHERE log_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting system log ID: " + id, e);
        }
    }

    @Override
    public boolean existsById(Integer id) throws DAOException {
        String sql = "SELECT 1 FROM system_logs WHERE log_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DAOException("Error checking system log existence: " + id, e);
        }
    }

    @Override
    public long count() throws DAOException {
        String sql = "SELECT COUNT(*) FROM system_logs";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            throw new DAOException("Error counting system logs", e);
        }
        return 0;
    }

    @Override
    public List<SystemLog> findRecentLogs(int limit) throws DAOException {
        String sql = BASE_SELECT + "ORDER BY l.timestamp DESC LIMIT ?";
        List<SystemLog> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit > 0 ? limit : 50);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding recent system logs", e);
        }
        return list;
    }

    @Override
    public List<SystemLog> findByUserId(int userId) throws DAOException {
        String sql = BASE_SELECT + "WHERE l.user_id = ? ORDER BY l.timestamp DESC";
        List<SystemLog> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding system logs for user: " + userId, e);
        }
        return list;
    }

    @Override
    public List<SystemLog> findByActionType(String actionType) throws DAOException {
        String sql = BASE_SELECT + "WHERE l.action_type = ? ORDER BY l.timestamp DESC";
        List<SystemLog> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, actionType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding system logs by action: " + actionType, e);
        }
        return list;
    }
}
