package com.example.car_rental_sys.dao.impl;

import com.example.car_rental_sys.dao.BankCardDAO;
import com.example.car_rental_sys.dao.DAOException;
import com.example.car_rental_sys.db.DatabaseConnection;
import com.example.car_rental_sys.model.BankCard;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Raw JDBC implementation of {@link BankCardDAO}.
 */
public class BankCardDAOImpl implements BankCardDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private BankCard mapResultSet(ResultSet rs) throws SQLException {
        BankCard b = new BankCard();
        b.setCardId(rs.getInt("card_id"));
        b.setUserId(rs.getInt("user_id"));
        b.setCardNumber(rs.getString("card_number"));
        b.setCardHolder(rs.getString("card_holder"));
        b.setExpiryDate(rs.getString("expiry_date"));
        b.setCvv(rs.getString("cvv"));
        b.setBalance(rs.getBigDecimal("balance"));
        b.setCardType(rs.getString("card_type"));
        b.setIsDefault(rs.getBoolean("is_default"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) b.setCreatedAt(created.toLocalDateTime());
        return b;
    }

    @Override
    public Optional<BankCard> findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM bank_cards WHERE card_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding bank card by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<BankCard> findAll() throws DAOException {
        String sql = "SELECT * FROM bank_cards ORDER BY card_id ASC";
        List<BankCard> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            throw new DAOException("Error retrieving bank cards", e);
        }
        return list;
    }

    @Override
    public BankCard save(BankCard card) throws DAOException {
        String sql = "INSERT INTO bank_cards (user_id, card_number, card_holder, expiry_date, cvv, balance, card_type, is_default) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, card.getUserId());
            stmt.setString(2, card.getCardNumber());
            stmt.setString(3, card.getCardHolder());
            stmt.setString(4, card.getExpiryDate());
            stmt.setString(5, card.getCvv());
            stmt.setBigDecimal(6, card.getBalance() != null ? card.getBalance() : BigDecimal.ZERO);
            stmt.setString(7, card.getCardType() != null ? card.getCardType() : "Visa");
            stmt.setBoolean(8, card.getIsDefault() != null ? card.getIsDefault() : false);

            stmt.executeUpdate();
            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) card.setCardId(gk.getInt(1));
            }
            return card;
        } catch (SQLException e) {
            throw new DAOException("Error saving bank card", e);
        }
    }

    @Override
    public boolean update(BankCard card) throws DAOException {
        String sql = "UPDATE bank_cards SET user_id = ?, card_number = ?, card_holder = ?, expiry_date = ?, " +
                     "cvv = ?, balance = ?, card_type = ?, is_default = ? WHERE card_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, card.getUserId());
            stmt.setString(2, card.getCardNumber());
            stmt.setString(3, card.getCardHolder());
            stmt.setString(4, card.getExpiryDate());
            stmt.setString(5, card.getCvv());
            stmt.setBigDecimal(6, card.getBalance());
            stmt.setString(7, card.getCardType());
            stmt.setBoolean(8, card.getIsDefault());
            stmt.setInt(9, card.getCardId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating bank card ID: " + card.getCardId(), e);
        }
    }

    @Override
    public boolean deleteById(Integer id) throws DAOException {
        String sql = "DELETE FROM bank_cards WHERE card_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting bank card ID: " + id, e);
        }
    }

    @Override
    public boolean existsById(Integer id) throws DAOException {
        String sql = "SELECT 1 FROM bank_cards WHERE card_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DAOException("Error checking card existence: " + id, e);
        }
    }

    @Override
    public long count() throws DAOException {
        String sql = "SELECT COUNT(*) FROM bank_cards";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            throw new DAOException("Error counting bank cards", e);
        }
        return 0;
    }

    @Override
    public List<BankCard> findByUserId(int userId) throws DAOException {
        String sql = "SELECT * FROM bank_cards WHERE user_id = ? ORDER BY is_default DESC, card_id ASC";
        List<BankCard> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding bank cards for user: " + userId, e);
        }
        return list;
    }

    @Override
    public Optional<BankCard> findDefaultCardByUserId(int userId) throws DAOException {
        String sql = "SELECT * FROM bank_cards WHERE user_id = ? AND is_default = TRUE LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding default card for user: " + userId, e);
        }
        return Optional.empty();
    }

    @Override
    public boolean updateBalance(int cardId, BigDecimal newBalance) throws DAOException {
        String sql = "UPDATE bank_cards SET balance = ? WHERE card_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newBalance);
            stmt.setInt(2, cardId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating balance for card ID: " + cardId, e);
        }
    }

    @Override
    public boolean setDefaultCard(int userId, int cardId) throws DAOException {
        String clearSql = "UPDATE bank_cards SET is_default = FALSE WHERE user_id = ?";
        String setSql = "UPDATE bank_cards SET is_default = TRUE WHERE card_id = ? AND user_id = ?";
        try (Connection conn = getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            try {
                conn.setAutoCommit(false);
                try (PreparedStatement clearStmt = conn.prepareStatement(clearSql);
                     PreparedStatement setStmt = conn.prepareStatement(setSql)) {
                    clearStmt.setInt(1, userId);
                    clearStmt.executeUpdate();

                    setStmt.setInt(1, cardId);
                    setStmt.setInt(2, userId);
                    int updated = setStmt.executeUpdate();

                    conn.commit();
                    return updated > 0;
                } catch (SQLException ex) {
                    conn.rollback();
                    throw ex;
                } finally {
                    conn.setAutoCommit(originalAutoCommit);
                }
            } catch (SQLException e) {
                throw new DAOException("Transaction failed setting default card for user: " + userId, e);
            }
        } catch (SQLException e) {
            throw new DAOException("Connection error setting default card", e);
        }
    }
}
