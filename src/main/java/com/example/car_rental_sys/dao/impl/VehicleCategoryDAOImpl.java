package com.example.car_rental_sys.dao.impl;

import com.example.car_rental_sys.dao.DAOException;
import com.example.car_rental_sys.dao.VehicleCategoryDAO;
import com.example.car_rental_sys.db.DatabaseConnection;
import com.example.car_rental_sys.model.VehicleCategory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Raw JDBC implementation of {@link VehicleCategoryDAO}.
 */
public class VehicleCategoryDAOImpl implements VehicleCategoryDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private VehicleCategory mapResultSet(ResultSet rs) throws SQLException {
        VehicleCategory cat = new VehicleCategory();
        cat.setCategoryId(rs.getInt("category_id"));
        cat.setName(rs.getString("name"));
        cat.setDescription(rs.getString("description"));
        return cat;
    }

    @Override
    public Optional<VehicleCategory> findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM vehicle_categories WHERE category_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding vehicle category by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<VehicleCategory> findAll() throws DAOException {
        String sql = "SELECT * FROM vehicle_categories ORDER BY category_id ASC";
        List<VehicleCategory> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error retrieving vehicle categories", e);
        }
        return list;
    }

    @Override
    public VehicleCategory save(VehicleCategory entity) throws DAOException {
        String sql = "INSERT INTO vehicle_categories (name, description) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getDescription());

            stmt.executeUpdate();
            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) {
                    entity.setCategoryId(gk.getInt(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new DAOException("Error creating vehicle category: " + entity.getName(), e);
        }
    }

    @Override
    public boolean update(VehicleCategory entity) throws DAOException {
        String sql = "UPDATE vehicle_categories SET name = ?, description = ? WHERE category_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getDescription());
            stmt.setInt(3, entity.getCategoryId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating category ID: " + entity.getCategoryId(), e);
        }
    }

    @Override
    public boolean deleteById(Integer id) throws DAOException {
        String sql = "DELETE FROM vehicle_categories WHERE category_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting category ID: " + id, e);
        }
    }

    @Override
    public boolean existsById(Integer id) throws DAOException {
        String sql = "SELECT 1 FROM vehicle_categories WHERE category_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DAOException("Error checking category existence: " + id, e);
        }
    }

    @Override
    public long count() throws DAOException {
        String sql = "SELECT COUNT(*) FROM vehicle_categories";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Error counting categories", e);
        }
        return 0;
    }

    @Override
    public Optional<VehicleCategory> findByName(String name) throws DAOException {
        String sql = "SELECT * FROM vehicle_categories WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding category by name: " + name, e);
        }
        return Optional.empty();
    }
}
