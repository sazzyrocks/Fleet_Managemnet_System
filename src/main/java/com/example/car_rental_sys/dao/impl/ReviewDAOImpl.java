package com.example.car_rental_sys.dao.impl;

import com.example.car_rental_sys.dao.DAOException;
import com.example.car_rental_sys.dao.ReviewDAO;
import com.example.car_rental_sys.db.DatabaseConnection;
import com.example.car_rental_sys.model.Review;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Raw JDBC implementation of {@link ReviewDAO}.
 */
public class ReviewDAOImpl implements ReviewDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private static final String BASE_SELECT =
            "SELECT r.*, u.full_name AS user_name " +
            "FROM reviews r " +
            "JOIN users u ON r.user_id = u.user_id ";

    private Review mapResultSet(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setReviewId(rs.getInt("review_id"));
        r.setOrderId(rs.getInt("order_id"));
        r.setUserId(rs.getInt("user_id"));
        r.setVehicleId(rs.getInt("vehicle_id"));
        r.setRating(rs.getInt("rating"));
        r.setCommentText(rs.getString("comment_text"));
        try { r.setUserName(rs.getString("user_name")); } catch (SQLException ignored) {}
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) r.setCreatedAt(created.toLocalDateTime());
        return r;
    }

    @Override
    public Optional<Review> findById(Integer id) throws DAOException {
        String sql = BASE_SELECT + "WHERE r.review_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding review by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Review> findAll() throws DAOException {
        String sql = BASE_SELECT + "ORDER BY r.review_id DESC";
        List<Review> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            throw new DAOException("Error retrieving reviews", e);
        }
        return list;
    }

    @Override
    public Review save(Review r) throws DAOException {
        String sql = "INSERT INTO reviews (order_id, user_id, vehicle_id, rating, comment_text) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, r.getOrderId());
            stmt.setInt(2, r.getUserId());
            stmt.setInt(3, r.getVehicleId());
            stmt.setInt(4, r.getRating() != null ? r.getRating() : 5);
            stmt.setString(5, r.getCommentText());

            stmt.executeUpdate();
            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) r.setReviewId(gk.getInt(1));
            }
            return r;
        } catch (SQLException e) {
            throw new DAOException("Error saving review", e);
        }
    }

    @Override
    public boolean update(Review r) throws DAOException {
        String sql = "UPDATE reviews SET order_id = ?, user_id = ?, vehicle_id = ?, rating = ?, comment_text = ? WHERE review_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, r.getOrderId());
            stmt.setInt(2, r.getUserId());
            stmt.setInt(3, r.getVehicleId());
            stmt.setInt(4, r.getRating());
            stmt.setString(5, r.getCommentText());
            stmt.setInt(6, r.getReviewId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating review ID: " + r.getReviewId(), e);
        }
    }

    @Override
    public boolean deleteById(Integer id) throws DAOException {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting review ID: " + id, e);
        }
    }

    @Override
    public boolean existsById(Integer id) throws DAOException {
        String sql = "SELECT 1 FROM reviews WHERE review_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DAOException("Error checking review existence: " + id, e);
        }
    }

    @Override
    public long count() throws DAOException {
        String sql = "SELECT COUNT(*) FROM reviews";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            throw new DAOException("Error counting reviews", e);
        }
        return 0;
    }

    @Override
    public List<Review> findByVehicleId(int vehicleId) throws DAOException {
        String sql = BASE_SELECT + "WHERE r.vehicle_id = ? ORDER BY r.created_at DESC";
        List<Review> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding reviews for vehicle: " + vehicleId, e);
        }
        return list;
    }

    @Override
    public List<Review> findByUserId(int userId) throws DAOException {
        String sql = BASE_SELECT + "WHERE r.user_id = ? ORDER BY r.created_at DESC";
        List<Review> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding reviews for user: " + userId, e);
        }
        return list;
    }

    @Override
    public double getAverageRatingForVehicle(int vehicleId) throws DAOException {
        String sql = "SELECT COALESCE(AVG(rating), 0.0) FROM reviews WHERE vehicle_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Error calculating average rating for vehicle: " + vehicleId, e);
        }
        return 0.0;
    }
}
