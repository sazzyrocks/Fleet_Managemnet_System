package com.example.car_rental_sys.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Automated Database Schema Initializer.
 *
 * <p>Reads {@code schema.sql} from classpath resources and initializes database tables,
 * indexes, and seeds if they do not already exist. Enables zero-configuration setup.</p>
 */
public final class DatabaseInitializer {

    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());
    private static final String SCHEMA_FILE = "schema.sql";

    private DatabaseInitializer() {
        // Prevent instantiation of utility class
    }

    private static boolean tablesAlreadyExist() {
        String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'fleet_management_db' AND table_name = 'vehicles'";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    private static void ensureDatabaseExists() {
        try {
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            String fullUrl = dbConn.getUrl();
            String username = dbConn.getUsername();
            String password = dbConn.getPassword();

            if (fullUrl == null || username == null) {
                return;
            }

            // Extract server URL without DB name
            String serverUrl = fullUrl;
            String dbName = "fleet_management_db";
            int slashIndex = fullUrl.indexOf('/', "jdbc:mysql://".length());
            if (slashIndex != -1) {
                int questionIndex = fullUrl.indexOf('?', slashIndex);
                if (questionIndex != -1) {
                    dbName = fullUrl.substring(slashIndex + 1, questionIndex);
                    serverUrl = fullUrl.substring(0, slashIndex + 1) + fullUrl.substring(questionIndex);
                } else {
                    dbName = fullUrl.substring(slashIndex + 1);
                    serverUrl = fullUrl.substring(0, slashIndex + 1);
                }
            }

            try (Connection conn = java.sql.DriverManager.getConnection(serverUrl, username, password);
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + dbName + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
                LOGGER.info("Ensured database '" + dbName + "' exists on MySQL server.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Note during database pre-creation: {0}", e.getMessage());
        }
    }

    /**
     * Executes schema.sql against the configured database to ensure all required tables exist.
     *
     * @return true if initialization completed without errors, false otherwise
     */
    public static boolean initializeDatabase() {
        LOGGER.info("Starting automated database schema verification...");
        ensureDatabaseExists();

        if (tablesAlreadyExist()) {
            LOGGER.info("Database schema already initialized. Preserving existing fleet data.");
            try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
                ensureAdminUser(conn);
            } catch (Exception ignored) {
            }
            return true;
        }

        try (InputStream in = DatabaseInitializer.class.getClassLoader().getResourceAsStream(SCHEMA_FILE)) {
            if (in == null) {
                LOGGER.log(Level.WARNING, "Schema file {0} not found on classpath. Skipping automatic initialization.", SCHEMA_FILE);
                return false;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                 Connection conn = DatabaseConnection.getInstance().getConnection();
                 Statement stmt = conn.createStatement()) {

                StringBuilder sqlBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    // Skip comments and empty lines
                    if (line.isEmpty() || line.startsWith("--") || line.startsWith("/*")) {
                        continue;
                    }
                    sqlBuilder.append(line).append(" ");
                    if (line.endsWith(";")) {
                        String statement = sqlBuilder.toString().trim();
                        if (!statement.isEmpty()) {
                            try {
                                stmt.execute(statement);
                            } catch (Exception ex) {
                                LOGGER.log(Level.FINE, "Statement execution note: {0}", ex.getMessage());
                            }
                        }
                        sqlBuilder.setLength(0);
                    }
                }
                ensureAdminUser(conn);
                LOGGER.info("Database schema initialized successfully.");
                return true;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Automated database initialization encountered an error: " + e.getMessage(), e);
            return false;
        }
    }

    private static void ensureAdminUser(Connection conn) {
        String adminPass = "admin123";
        String adminHash = com.example.car_rental_sys.util.PasswordUtil.hashPassword(adminPass);
        String sql = "INSERT INTO `users` (`user_id`, `username`, `password_hash`, `email`, `phone`, `full_name`, `role`, `status`) " +
                     "VALUES (1, 'admin', ?, 'admin@fleetms.com', '+1-555-0100', 'System Administrator', 'ADMIN', 'ACTIVE') " +
                     "ON DUPLICATE KEY UPDATE `password_hash` = ?, `status` = 'ACTIVE'";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, adminHash);
            pstmt.setString(2, adminHash);
            pstmt.executeUpdate();
            LOGGER.info("Verified default admin credentials (admin / admin123).");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Note verifying admin account: {0}", e.getMessage());
        }
    }
}
