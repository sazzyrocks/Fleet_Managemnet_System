package com.example.car_rental_sys.dao.impl;

import com.example.car_rental_sys.dao.DAOException;
import com.example.car_rental_sys.dao.DriverDAO;
import com.example.car_rental_sys.db.DatabaseConnection;
import com.example.car_rental_sys.model.Driver;
import com.example.car_rental_sys.model.DriverStatus;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Raw JDBC implementation of {@link DriverDAO}.
 */
public class DriverDAOImpl implements DriverDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private static final String BASE_SELECT =
            "SELECT d.*, u.full_name AS driver_name, u.phone " +
            "FROM drivers d " +
            "JOIN users u ON d.user_id = u.user_id ";

    private Driver mapResultSet(ResultSet rs) throws SQLException {
        Driver d = new Driver();
        d.setDriverId(rs.getInt("driver_id"));
        d.setUserId(rs.getInt("user_id"));
        d.setLicenseNumber(rs.getString("license_number"));
        d.setExperienceYears(rs.getInt("experience_years"));
        d.setRating(rs.getBigDecimal("rating"));
        d.setStatus(DriverStatus.fromString(rs.getString("status")));

        try { d.setDriverName(rs.getString("driver_name")); } catch (SQLException ignored) {}
        try { d.setPhone(rs.getString("phone")); } catch (SQLException ignored) {}

        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) d.setCreatedAt(created.toLocalDateTime());
        return d;
    }

    @Override
    public Optional<Driver> findById(Integer id) throws DAOException {
        String sql = BASE_SELECT + "WHERE d.driver_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding driver by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Driver> findAll() throws DAOException {
        String sql = BASE_SELECT + "ORDER BY d.driver_id ASC";
        List<Driver> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            throw new DAOException("Error retrieving drivers", e);
        }
        return list;
    }

    @Override
    public Driver save(Driver d) throws DAOException {
        String sql = "INSERT INTO drivers (user_id, license_number, experience_years, rating, status) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, d.getUserId());
            stmt.setString(2, d.getLicenseNumber());
            stmt.setInt(3, d.getExperienceYears() != null ? d.getExperienceYears() : 1);
            stmt.setBigDecimal(4, d.getRating() != null ? d.getRating() : new BigDecimal("5.00"));
            stmt.setString(5, d.getStatus() != null ? d.getStatus().name() : DriverStatus.AVAILABLE.name());

            stmt.executeUpdate();
            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) d.setDriverId(gk.getInt(1));
            }
            return d;
        } catch (SQLException e) {
            throw new DAOException("Error creating driver record", e);
        }
    }

    @Override
    public boolean update(Driver d) throws DAOException {
        String sql = "UPDATE drivers SET user_id = ?, license_number = ?, experience_years = ?, " +
                     "rating = ?, status = ? WHERE driver_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, d.getUserId());
            stmt.setString(2, d.getLicenseNumber());
            stmt.setInt(3, d.getExperienceYears());
            stmt.setBigDecimal(4, d.getRating());
            stmt.setString(5, d.getStatus() != null ? d.getStatus().name() : DriverStatus.AVAILABLE.name());
            stmt.setInt(6, d.getDriverId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating driver ID: " + d.getDriverId(), e);
        }
    }

    @Override
    public boolean deleteById(Integer id) throws DAOException {
        String sql = "DELETE FROM drivers WHERE driver_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting driver ID: " + id, e);
        }
    }

    @Override
    public boolean existsById(Integer id) throws DAOException {
        String sql = "SELECT 1 FROM drivers WHERE driver_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DAOException("Error checking driver existence: " + id, e);
        }
    }

    @Override
    public long count() throws DAOException {
        String sql = "SELECT COUNT(*) FROM drivers";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            throw new DAOException("Error counting drivers", e);
        }
        return 0;
    }

    @Override
    public Optional<Driver> findByUserId(int userId) throws DAOException {
        String sql = BASE_SELECT + "WHERE d.user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding driver by user ID: " + userId, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Driver> findByLicenseNumber(String licenseNumber) throws DAOException {
        String sql = BASE_SELECT + "WHERE d.license_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licenseNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding driver by license: " + licenseNumber, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Driver> findAvailableDrivers() throws DAOException {
        String sql = BASE_SELECT + "WHERE d.status = 'AVAILABLE' ORDER BY d.rating DESC";
        List<Driver> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            throw new DAOException("Error finding available drivers", e);
        }
        return list;
    }

    @Override
    public boolean updateStatus(int driverId, DriverStatus status) throws DAOException {
        String sql = "UPDATE drivers SET status = ? WHERE driver_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status != null ? status.name() : DriverStatus.AVAILABLE.name());
            stmt.setInt(2, driverId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating driver status for ID: " + driverId, e);
        }
    }

    @Override
    public boolean updateRating(int driverId, BigDecimal rating) throws DAOException {
        String sql = "UPDATE drivers SET rating = ? WHERE driver_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, rating);
            stmt.setInt(2, driverId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating driver rating for ID: " + driverId, e);
        }
    }
}
