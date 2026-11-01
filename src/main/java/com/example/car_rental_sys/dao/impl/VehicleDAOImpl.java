package com.example.car_rental_sys.dao.impl;

import com.example.car_rental_sys.dao.DAOException;
import com.example.car_rental_sys.dao.VehicleDAO;
import com.example.car_rental_sys.db.DatabaseConnection;
import com.example.car_rental_sys.model.FuelType;
import com.example.car_rental_sys.model.TransmissionType;
import com.example.car_rental_sys.model.Vehicle;
import com.example.car_rental_sys.model.VehicleStatus;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Raw JDBC implementation of {@link VehicleDAO} featuring parameterized multi-criteria searches.
 */
public class VehicleDAOImpl implements VehicleDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private Vehicle mapResultSet(ResultSet rs) throws SQLException {
        Vehicle v = new Vehicle();
        v.setVehicleId(rs.getInt("vehicle_id"));
        int catId = rs.getInt("category_id");
        if (!rs.wasNull()) {
            v.setCategoryId(catId);
        }
        v.setBrand(rs.getString("brand"));
        v.setModel(rs.getString("model"));
        v.setYear(rs.getInt("year"));
        v.setLicensePlate(rs.getString("license_plate"));
        v.setColor(rs.getString("color"));
        v.setSeats(rs.getInt("seats"));
        v.setTransmission(TransmissionType.fromString(rs.getString("transmission")));
        v.setFuelType(FuelType.fromString(rs.getString("fuel_type")));
        v.setDailyRate(rs.getBigDecimal("daily_rate"));
        v.setMileage(rs.getInt("mileage"));
        v.setStatus(VehicleStatus.fromString(rs.getString("status")));
        v.setImagePath(rs.getString("image_path"));

        // Optional JOIN column
        try {
            v.setCategoryName(rs.getString("category_name"));
        } catch (SQLException ignored) {
            // Column may not be in projection
        }

        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) v.setCreatedAt(created.toLocalDateTime());
        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) v.setUpdatedAt(updated.toLocalDateTime());

        return v;
    }

    private static final String BASE_SELECT =
            "SELECT v.*, c.name AS category_name " +
            "FROM vehicles v " +
            "LEFT JOIN vehicle_categories c ON v.category_id = c.category_id ";

    @Override
    public Optional<Vehicle> findById(Integer id) throws DAOException {
        String sql = BASE_SELECT + "WHERE v.vehicle_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding vehicle by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Vehicle> findAll() throws DAOException {
        String sql = BASE_SELECT + "ORDER BY v.vehicle_id ASC";
        List<Vehicle> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error retrieving all vehicles", e);
        }
        return list;
    }

    @Override
    public Vehicle save(Vehicle v) throws DAOException {
        String sql = "INSERT INTO vehicles (category_id, brand, model, year, license_plate, color, " +
                     "seats, transmission, fuel_type, daily_rate, mileage, status, image_path) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (v.getCategoryId() != null) {
                stmt.setInt(1, v.getCategoryId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setString(2, v.getBrand());
            stmt.setString(3, v.getModel());
            stmt.setInt(4, v.getYear());
            stmt.setString(5, v.getLicensePlate());
            stmt.setString(6, v.getColor());
            stmt.setInt(7, v.getSeats() != null ? v.getSeats() : 5);
            stmt.setString(8, v.getTransmission() != null ? v.getTransmission().name() : TransmissionType.AUTOMATIC.name());
            stmt.setString(9, v.getFuelType() != null ? v.getFuelType().name() : FuelType.PETROL.name());
            stmt.setBigDecimal(10, v.getDailyRate() != null ? v.getDailyRate() : BigDecimal.ZERO);
            stmt.setInt(11, v.getMileage() != null ? v.getMileage() : 0);
            stmt.setString(12, v.getStatus() != null ? v.getStatus().name() : VehicleStatus.AVAILABLE.name());
            stmt.setString(13, v.getImagePath());

            stmt.executeUpdate();
            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) {
                    v.setVehicleId(gk.getInt(1));
                }
            }
            return v;
        } catch (SQLException e) {
            throw new DAOException("Error creating vehicle: " + v.getLicensePlate(), e);
        }
    }

    @Override
    public boolean update(Vehicle v) throws DAOException {
        String sql = "UPDATE vehicles SET category_id = ?, brand = ?, model = ?, year = ?, license_plate = ?, " +
                     "color = ?, seats = ?, transmission = ?, fuel_type = ?, daily_rate = ?, mileage = ?, " +
                     "status = ?, image_path = ? WHERE vehicle_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (v.getCategoryId() != null) {
                stmt.setInt(1, v.getCategoryId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setString(2, v.getBrand());
            stmt.setString(3, v.getModel());
            stmt.setInt(4, v.getYear());
            stmt.setString(5, v.getLicensePlate());
            stmt.setString(6, v.getColor());
            stmt.setInt(7, v.getSeats());
            stmt.setString(8, v.getTransmission() != null ? v.getTransmission().name() : TransmissionType.AUTOMATIC.name());
            stmt.setString(9, v.getFuelType() != null ? v.getFuelType().name() : FuelType.PETROL.name());
            stmt.setBigDecimal(10, v.getDailyRate());
            stmt.setInt(11, v.getMileage());
            stmt.setString(12, v.getStatus() != null ? v.getStatus().name() : VehicleStatus.AVAILABLE.name());
            stmt.setString(13, v.getImagePath());
            stmt.setInt(14, v.getVehicleId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating vehicle ID: " + v.getVehicleId(), e);
        }
    }

    @Override
    public boolean deleteById(Integer id) throws DAOException {
        String sql = "DELETE FROM vehicles WHERE vehicle_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting vehicle ID: " + id, e);
        }
    }

    @Override
    public boolean existsById(Integer id) throws DAOException {
        String sql = "SELECT 1 FROM vehicles WHERE vehicle_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DAOException("Error checking vehicle existence: " + id, e);
        }
    }

    @Override
    public long count() throws DAOException {
        String sql = "SELECT COUNT(*) FROM vehicles";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Error counting vehicles", e);
        }
        return 0;
    }

    @Override
    public Optional<Vehicle> findByLicensePlate(String licensePlate) throws DAOException {
        String sql = BASE_SELECT + "WHERE v.license_plate = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licensePlate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding vehicle by plate: " + licensePlate, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Vehicle> findByStatus(VehicleStatus status) throws DAOException {
        String sql = BASE_SELECT + "WHERE v.status = ? ORDER BY v.vehicle_id ASC";
        List<Vehicle> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status != null ? status.name() : VehicleStatus.AVAILABLE.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding vehicles by status: " + status, e);
        }
        return list;
    }

    @Override
    public List<Vehicle> findByCategoryId(int categoryId) throws DAOException {
        String sql = BASE_SELECT + "WHERE v.category_id = ? ORDER BY v.vehicle_id ASC";
        List<Vehicle> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding vehicles by category: " + categoryId, e);
        }
        return list;
    }

    @Override
    public List<Vehicle> searchVehicles(String keyword, Integer categoryId, BigDecimal maxRate, VehicleStatus status) throws DAOException {
        StringBuilder sql = new StringBuilder(BASE_SELECT).append("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (status != null) {
            sql.append("AND v.status = ? ");
            params.add(status.name());
        }
        if (categoryId != null && categoryId > 0) {
            sql.append("AND v.category_id = ? ");
            params.add(categoryId);
        }
        if (maxRate != null && maxRate.compareTo(BigDecimal.ZERO) > 0) {
            sql.append("AND v.daily_rate <= ? ");
            params.add(maxRate);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (v.brand LIKE ? OR v.model LIKE ? OR v.color LIKE ?) ");
            String wildcard = "%" + keyword.trim() + "%";
            params.add(wildcard);
            params.add(wildcard);
            params.add(wildcard);
        }
        sql.append("ORDER BY v.daily_rate ASC");

        List<Vehicle> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error searching vehicles with criteria", e);
        }
        return list;
    }

    @Override
    public boolean updateStatus(int vehicleId, VehicleStatus status) throws DAOException {
        String sql = "UPDATE vehicles SET status = ? WHERE vehicle_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status != null ? status.name() : VehicleStatus.AVAILABLE.name());
            stmt.setInt(2, vehicleId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating vehicle status: " + vehicleId, e);
        }
    }

    @Override
    public boolean updateMileage(int vehicleId, int newMileage) throws DAOException {
        String sql = "UPDATE vehicles SET mileage = ? WHERE vehicle_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newMileage);
            stmt.setInt(2, vehicleId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating mileage for vehicle: " + vehicleId, e);
        }
    }

    @Override
    public long countByStatus(VehicleStatus status) throws DAOException {
        String sql = "SELECT COUNT(*) FROM vehicles WHERE status = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status != null ? status.name() : VehicleStatus.AVAILABLE.name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error counting vehicles by status: " + status, e);
        }
        return 0;
    }
}
