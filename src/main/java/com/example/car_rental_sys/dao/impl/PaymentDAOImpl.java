package com.example.car_rental_sys.dao.impl;

import com.example.car_rental_sys.dao.DAOException;
import com.example.car_rental_sys.dao.PaymentDAO;
import com.example.car_rental_sys.db.DatabaseConnection;
import com.example.car_rental_sys.model.Payment;
import com.example.car_rental_sys.model.PaymentStatus;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Raw JDBC implementation of {@link PaymentDAO}.
 */
public class PaymentDAOImpl implements PaymentDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private Payment mapResultSet(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setPaymentId(rs.getInt("payment_id"));
        p.setOrderId(rs.getInt("order_id"));
        p.setAmount(rs.getBigDecimal("amount"));
        p.setPaymentMethod(rs.getString("payment_method"));
        p.setTransactionReference(rs.getString("transaction_reference"));
        p.setPaymentStatus(PaymentStatus.fromString(rs.getString("payment_status")));
        Timestamp paidAt = rs.getTimestamp("paid_at");
        if (paidAt != null) p.setPaidAt(paidAt.toLocalDateTime());
        return p;
    }

    @Override
    public Optional<Payment> findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM payments WHERE payment_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding payment by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Payment> findAll() throws DAOException {
        String sql = "SELECT * FROM payments ORDER BY payment_id DESC";
        List<Payment> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            throw new DAOException("Error retrieving payments", e);
        }
        return list;
    }

    @Override
    public Payment save(Payment p) throws DAOException {
        String sql = "INSERT INTO payments (order_id, amount, payment_method, transaction_reference, payment_status) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, p.getOrderId());
            stmt.setBigDecimal(2, p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO);
            stmt.setString(3, p.getPaymentMethod() != null ? p.getPaymentMethod() : "CARD");
            stmt.setString(4, p.getTransactionReference());
            stmt.setString(5, p.getPaymentStatus() != null ? p.getPaymentStatus().name() : PaymentStatus.PENDING.name());

            stmt.executeUpdate();
            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) p.setPaymentId(gk.getInt(1));
            }
            return p;
        } catch (SQLException e) {
            throw new DAOException("Error recording payment for order: " + p.getOrderId(), e);
        }
    }

    @Override
    public boolean update(Payment p) throws DAOException {
        String sql = "UPDATE payments SET order_id = ?, amount = ?, payment_method = ?, " +
                     "transaction_reference = ?, payment_status = ? WHERE payment_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, p.getOrderId());
            stmt.setBigDecimal(2, p.getAmount());
            stmt.setString(3, p.getPaymentMethod());
            stmt.setString(4, p.getTransactionReference());
            stmt.setString(5, p.getPaymentStatus() != null ? p.getPaymentStatus().name() : PaymentStatus.PENDING.name());
            stmt.setInt(6, p.getPaymentId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating payment ID: " + p.getPaymentId(), e);
        }
    }

    @Override
    public boolean deleteById(Integer id) throws DAOException {
        String sql = "DELETE FROM payments WHERE payment_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting payment ID: " + id, e);
        }
    }

    @Override
    public boolean existsById(Integer id) throws DAOException {
        String sql = "SELECT 1 FROM payments WHERE payment_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DAOException("Error checking payment existence: " + id, e);
        }
    }

    @Override
    public long count() throws DAOException {
        String sql = "SELECT COUNT(*) FROM payments";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            throw new DAOException("Error counting payments", e);
        }
        return 0;
    }

    @Override
    public Optional<Payment> findByOrderId(int orderId) throws DAOException {
        String sql = "SELECT * FROM payments WHERE order_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding payment for order ID: " + orderId, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Payment> findByTransactionReference(String reference) throws DAOException {
        String sql = "SELECT * FROM payments WHERE transaction_reference = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reference);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding payment by reference: " + reference, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Payment> findByStatus(PaymentStatus status) throws DAOException {
        String sql = "SELECT * FROM payments WHERE payment_status = ? ORDER BY payment_id DESC";
        List<Payment> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status != null ? status.name() : PaymentStatus.PENDING.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding payments by status: " + status, e);
        }
        return list;
    }

    @Override
    public boolean updateStatus(int paymentId, PaymentStatus status) throws DAOException {
        String sql = "UPDATE payments SET payment_status = ? WHERE payment_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status != null ? status.name() : PaymentStatus.PENDING.name());
            stmt.setInt(2, paymentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating payment status: " + paymentId, e);
        }
    }
}
