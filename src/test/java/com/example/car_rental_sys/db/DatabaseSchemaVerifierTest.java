package com.example.car_rental_sys.db;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verification Test for Database Schema definition and seed data integrity.
 */
class DatabaseSchemaVerifierTest {

    private static final String SCHEMA_FILE = "schema.sql";

    private static final List<String> EXPECTED_TABLES = List.of(
            "users",
            "bank_cards",
            "vehicle_categories",
            "vehicles",
            "orders",
            "payments",
            "drivers",
            "vehicle_diagnostics",
            "reviews",
            "messages",
            "system_logs",
            "todos"
    );

    @Test
    @DisplayName("Verify schema.sql is present on the classpath and readable")
    void testSchemaFileExists() {
        InputStream in = getClass().getClassLoader().getResourceAsStream(SCHEMA_FILE);
        assertNotNull(in, "schema.sql must be located on the classpath in src/main/resources/");
    }

    @Test
    @DisplayName("Verify schema.sql defines all 12 enterprise tables")
    void testSchemaContainsAllRequiredTables() throws Exception {
        Set<String> createdTables = new HashSet<>();

        try (InputStream in = getClass().getClassLoader().getResourceAsStream(SCHEMA_FILE);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (line.startsWith("create table")) {
                    for (String table : EXPECTED_TABLES) {
                        if (line.contains("`" + table + "`") || line.contains(" " + table + " ") || line.contains(" " + table + "(")) {
                            createdTables.add(table);
                        }
                    }
                }
            }
        }

        for (String expected : EXPECTED_TABLES) {
            assertTrue(createdTables.contains(expected),
                    "schema.sql is missing table definition for: " + expected);
        }
        assertEquals(EXPECTED_TABLES.size(), createdTables.size(), "All 12 tables must be defined in schema.sql");
    }

    @Test
    @DisplayName("Verify schema.sql includes default seeds for categories and admin")
    void testSchemaContainsSeedData() throws Exception {
        boolean hasCategorySeeds = false;
        boolean hasAdminSeed = false;

        try (InputStream in = getClass().getClassLoader().getResourceAsStream(SCHEMA_FILE);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (line.startsWith("insert into") && line.contains("vehicle_categories")) {
                    hasCategorySeeds = true;
                }
                if (line.startsWith("insert into") && line.contains("users") && line.contains("admin")) {
                    hasAdminSeed = true;
                }
            }
        }

        assertTrue(hasCategorySeeds, "schema.sql must seed vehicle categories");
        assertTrue(hasAdminSeed, "schema.sql must seed the default admin account");
    }
}

