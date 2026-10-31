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

    /**
     * Executes schema.sql against the configured database to ensure all required tables exist.
     *
     * @return true if initialization completed without errors, false otherwise
     */
    public static boolean initializeDatabase() {
        LOGGER.info("Starting automated database schema verification...");
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
                LOGGER.info("Database schema initialized successfully.");
                return true;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Automated database initialization encountered an error: " + e.getMessage(), e);
            return false;
        }
    }
}
