package com.example.car_rental_sys.db;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests verifying the database configuration and schema presence.
 */
class DatabaseConfigTest {

    @Test
    @DisplayName("db.properties should exist on classpath with valid configuration keys")
    void shouldFindDatabasePropertiesOnClasspath() throws Exception {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            assertNotNull(in, "db.properties must be packaged in resources");

            Properties props = new Properties();
            props.load(in);

            assertNotNull(props.getProperty("db.url"), "db.url must be specified");
            assertNotNull(props.getProperty("db.driver"), "db.driver must be specified");
            assertEquals("com.mysql.cj.jdbc.Driver", props.getProperty("db.driver"));
        }
    }

    @Test
    @DisplayName("schema.sql should exist on classpath with DDL table definitions")
    void shouldFindSchemaSqlOnClasspath() throws Exception {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("schema.sql")) {
            assertNotNull(in, "schema.sql must be packaged in resources for auto-bootstrap");
            assertTrue(in.available() > 0, "schema.sql must not be empty");
        }
    }
}
