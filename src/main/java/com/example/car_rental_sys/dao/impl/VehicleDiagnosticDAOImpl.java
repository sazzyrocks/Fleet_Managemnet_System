package com.example.car_rental_sys.dao.impl;

import com.example.car_rental_sys.dao.DAOException;
import com.example.car_rental_sys.dao.VehicleDiagnosticDAO;
import com.example.car_rental_sys.db.DatabaseConnection;
import com.example.car_rental_sys.model.VehicleDiagnostic;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Raw JDBC implementation of {@link VehicleDiagnosticDAO}.
 */
public class VehicleDiagnosticDAOImpl implements VehicleDiagnosticDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private VehicleDiagnostic mapResultSet(ResultSet rs) throws SQLException {
        VehicleDiagnostic d = new VehicleDiagnostic();
        d.setDiagId(rs.getInt("diag_id"));
        d.setVehicleId(rs.getInt("vehicle_id"));
        d.setEngineTemperature(rs.getBigDecimal("engine_temperature"));
        d.setTirePressureFrontLeft(rs.getBigDecimal("tire_pressure_front_left"));
        d.setTirePressureFrontRight(rs.getBigDecimal("tire_pressure_front_right"));
        d.setTirePressureRearLeft(rs.getBigDecimal("tire_pressure_rear_left"));
        d.setTirePressureRearRight(rs.getBigDecimal("tire_pressure_rear_right"));
        d.setBatteryHealthPercent(rs.getInt("battery_health_percent"));
        d.setFuelLevelPercent(rs.getInt("fuel_level_percent"));
        Timestamp rec = rs.getTimestamp("recorded_at");
        if (rec != null) d.setRecordedAt(rec.toLocalDateTime());
        return d;
    }

    @Override
    public Optional<VehicleDiagnostic> findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM vehicle_diagnostics WHERE diag_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding diagnostic by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<VehicleDiagnostic> findAll() throws DAOException {
        String sql = "SELECT * FROM vehicle_diagnostics ORDER BY diag_id DESC";
        List<VehicleDiagnostic> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            throw new DAOException("Error retrieving diagnostics", e);
        }
        return list;
    }

    @Override
    public VehicleDiagnostic save(VehicleDiagnostic d) throws DAOException {
        String sql = "INSERT INTO vehicle_diagnostics (vehicle_id, engine_temperature, tire_pressure_front_left, " +
                     "tire_pressure_front_right, tire_pressure_rear_left, tire_pressure_rear_right, " +
                     "battery_health_percent, fuel_level_percent) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, d.getVehicleId());
            stmt.setBigDecimal(2, d.getEngineTemperature() != null ? d.getEngineTemperature() : new BigDecimal("90.0"));
            stmt.setBigDecimal(3, d.getTirePressureFrontLeft() != null ? d.getTirePressureFrontLeft() : new BigDecimal("32.0"));
            stmt.setBigDecimal(4, d.getTirePressureFrontRight() != null ? d.getTirePressureFrontRight() : new BigDecimal("32.0"));
            stmt.setBigDecimal(5, d.getTirePressureRearLeft() != null ? d.getTirePressureRearLeft() : new BigDecimal("32.0"));
            stmt.setBigDecimal(6, d.getTirePressureRearRight() != null ? d.getTirePressureRearRight() : new BigDecimal("32.0"));
            stmt.setInt(7, d.getBatteryHealthPercent() != null ? d.getBatteryHealthPercent() : 100);
            stmt.setInt(8, d.getFuelLevelPercent() != null ? d.getFuelLevelPercent() : 100);

            stmt.executeUpdate();
            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) d.setDiagId(gk.getInt(1));
            }
            return d;
        } catch (SQLException e) {
            throw new DAOException("Error saving diagnostic telemetry for vehicle: " + d.getVehicleId(), e);
        }
    }

    @Override
    public boolean update(VehicleDiagnostic d) throws DAOException {
        String sql = "UPDATE vehicle_diagnostics SET vehicle_id = ?, engine_temperature = ?, " +
                     "tire_pressure_front_left = ?, tire_pressure_front_right = ?, tire_pressure_rear_left = ?, " +
                     "tire_pressure_rear_right = ?, battery_health_percent = ?, fuel_level_percent = ? " +
                     "WHERE diag_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, d.getVehicleId());
            stmt.setBigDecimal(2, d.getEngineTemperature());
            stmt.setBigDecimal(3, d.getTirePressureFrontLeft());
            stmt.setBigDecimal(4, d.getTirePressureFrontRight());
            stmt.setBigDecimal(5, d.getTirePressureRearLeft());
            stmt.setBigDecimal(6, d.getTirePressureRearRight());
            stmt.setInt(7, d.getBatteryHealthPercent());
            stmt.setInt(8, d.getFuelLevelPercent());
            stmt.setInt(9, d.getDiagId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating diagnostic telemetry ID: " + d.getDiagId(), e);
        }
    }

    @Override
    public boolean deleteById(Integer id) throws DAOException {
        String sql = "DELETE FROM vehicle_diagnostics WHERE diag_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting diagnostic ID: " + id, e);
        }
    }

    @Override
    public boolean existsById(Integer id) throws DAOException {
        String sql = "SELECT 1 FROM vehicle_diagnostics WHERE diag_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DAOException("Error checking diagnostic existence: " + id, e);
        }
    }

    @Override
    public long count() throws DAOException {
        String sql = "SELECT COUNT(*) FROM vehicle_diagnostics";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            throw new DAOException("Error counting diagnostics", e);
        }
        return 0;
    }

    @Override
    public Optional<VehicleDiagnostic> findLatestByVehicleId(int vehicleId) throws DAOException {
        String sql = "SELECT * FROM vehicle_diagnostics WHERE vehicle_id = ? ORDER BY recorded_at DESC LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding latest diagnostic for vehicle: " + vehicleId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<VehicleDiagnostic> findHistoryByVehicleId(int vehicleId, int limit) throws DAOException {
        String sql = "SELECT * FROM vehicle_diagnostics WHERE vehicle_id = ? ORDER BY recorded_at DESC LIMIT ?";
        List<VehicleDiagnostic> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            stmt.setInt(2, limit > 0 ? limit : 50);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding diagnostic history for vehicle: " + vehicleId, e);
        }
        return list;
    }

    @Override
    public List<VehicleDiagnostic> findOverheatingVehicles() throws DAOException {
        String sql = "SELECT * FROM vehicle_diagnostics WHERE engine_temperature > 105.0 ORDER BY recorded_at DESC";
        List<VehicleDiagnostic> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            throw new DAOException("Error finding overheating diagnostics", e);
        }
        return list;
    }
}
