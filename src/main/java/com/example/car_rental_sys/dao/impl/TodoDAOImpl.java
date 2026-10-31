package com.example.car_rental_sys.dao.impl;

import com.example.car_rental_sys.dao.DAOException;
import com.example.car_rental_sys.dao.TodoDAO;
import com.example.car_rental_sys.db.DatabaseConnection;
import com.example.car_rental_sys.model.Todo;
import com.example.car_rental_sys.model.TodoPriority;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Raw JDBC implementation of {@link TodoDAO}.
 */
public class TodoDAOImpl implements TodoDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private Todo mapResultSet(ResultSet rs) throws SQLException {
        Todo t = new Todo();
        t.setTodoId(rs.getInt("todo_id"));
        t.setUserId(rs.getInt("user_id"));
        t.setTitle(rs.getString("title"));
        t.setDescription(rs.getString("description"));
        t.setPriority(TodoPriority.fromString(rs.getString("priority")));
        t.setIsCompleted(rs.getBoolean("is_completed"));
        Date due = rs.getDate("due_date");
        if (due != null) t.setDueDate(due.toLocalDate());
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) t.setCreatedAt(created.toLocalDateTime());
        return t;
    }

    @Override
    public Optional<Todo> findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM todos WHERE todo_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding todo by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Todo> findAll() throws DAOException {
        String sql = "SELECT * FROM todos ORDER BY is_completed ASC, due_date ASC";
        List<Todo> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            throw new DAOException("Error retrieving todos", e);
        }
        return list;
    }

    @Override
    public Todo save(Todo t) throws DAOException {
        String sql = "INSERT INTO todos (user_id, title, description, priority, is_completed, due_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, t.getUserId());
            stmt.setString(2, t.getTitle());
            stmt.setString(3, t.getDescription());
            stmt.setString(4, t.getPriority() != null ? t.getPriority().name() : TodoPriority.MEDIUM.name());
            stmt.setBoolean(5, t.getIsCompleted() != null ? t.getIsCompleted() : false);
            if (t.getDueDate() != null) stmt.setDate(6, Date.valueOf(t.getDueDate()));
            else stmt.setNull(6, Types.DATE);

            stmt.executeUpdate();
            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) t.setTodoId(gk.getInt(1));
            }
            return t;
        } catch (SQLException e) {
            throw new DAOException("Error creating todo: " + t.getTitle(), e);
        }
    }

    @Override
    public boolean update(Todo t) throws DAOException {
        String sql = "UPDATE todos SET user_id = ?, title = ?, description = ?, priority = ?, " +
                     "is_completed = ?, due_date = ? WHERE todo_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, t.getUserId());
            stmt.setString(2, t.getTitle());
            stmt.setString(3, t.getDescription());
            stmt.setString(4, t.getPriority() != null ? t.getPriority().name() : TodoPriority.MEDIUM.name());
            stmt.setBoolean(5, t.getIsCompleted());
            if (t.getDueDate() != null) stmt.setDate(6, Date.valueOf(t.getDueDate()));
            else stmt.setNull(6, Types.DATE);
            stmt.setInt(7, t.getTodoId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating todo ID: " + t.getTodoId(), e);
        }
    }

    @Override
    public boolean deleteById(Integer id) throws DAOException {
        String sql = "DELETE FROM todos WHERE todo_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting todo ID: " + id, e);
        }
    }

    @Override
    public boolean existsById(Integer id) throws DAOException {
        String sql = "SELECT 1 FROM todos WHERE todo_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DAOException("Error checking todo existence: " + id, e);
        }
    }

    @Override
    public long count() throws DAOException {
        String sql = "SELECT COUNT(*) FROM todos";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            throw new DAOException("Error counting todos", e);
        }
        return 0;
    }

    @Override
    public List<Todo> findByUserId(int userId) throws DAOException {
        String sql = "SELECT * FROM todos WHERE user_id = ? ORDER BY is_completed ASC, due_date ASC";
        List<Todo> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding todos for user: " + userId, e);
        }
        return list;
    }

    @Override
    public List<Todo> findPendingByUserId(int userId) throws DAOException {
        String sql = "SELECT * FROM todos WHERE user_id = ? AND is_completed = FALSE ORDER BY due_date ASC";
        List<Todo> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding pending todos for user: " + userId, e);
        }
        return list;
    }

    @Override
    public boolean markAsCompleted(int todoId, boolean isCompleted) throws DAOException {
        String sql = "UPDATE todos SET is_completed = ? WHERE todo_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, isCompleted);
            stmt.setInt(2, todoId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error toggling completion for todo ID: " + todoId, e);
        }
    }
}
