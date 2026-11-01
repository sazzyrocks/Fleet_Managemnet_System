package com.example.car_rental_sys.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Architectural and Contract Verification Test for JavaFX FXML Views and Controllers.
 * Validates that every FXML view binds to a concrete, valid controller class,
 * and that all declared fx:id and onAction event handlers exist.
 */
class ControllerArchitectureTest {

    private static final String FXML_DIR = "src/main/resources/com/example/car_rental_sys/fxml";

    @Test
    @DisplayName("Verify every FXML file references a valid controller class")
    void testAllFxmlHaveResolvableControllers() throws IOException {
        File fxmlFolder = new File(FXML_DIR);
        assertTrue(fxmlFolder.exists() && fxmlFolder.isDirectory(), "FXML directory must exist");

        List<Path> fxmlFiles = Files.walk(fxmlFolder.toPath())
                .filter(p -> p.toString().endsWith(".fxml"))
                .collect(Collectors.toList());

        assertFalse(fxmlFiles.isEmpty(), "At least one FXML file should be found");

        Pattern controllerPattern = Pattern.compile("fx:controller=\"([^\"]+)\"");

        for (Path fxmlPath : fxmlFiles) {
            String content = Files.readString(fxmlPath);
            Matcher matcher = controllerPattern.matcher(content);

            if (matcher.find()) {
                String controllerClassName = matcher.group(1);
                assertDoesNotThrow(() -> {
                    Class<?> controllerClass = Class.forName(controllerClassName);
                    assertNotNull(controllerClass, "Controller class could not be resolved: " + controllerClassName);
                    // Verify no-arg constructor
                    assertNotNull(controllerClass.getDeclaredConstructor(),
                            "Controller must provide a no-argument constructor: " + controllerClassName);
                }, "Failed to load controller for " + fxmlPath.getFileName() + ": " + controllerClassName);
            }
        }
    }

    @Test
    @DisplayName("Verify FXML fx:id bindings correspond to declared controller fields")
    void testFxmlFieldBindingsExistInControllers() throws IOException {
        File fxmlFolder = new File(FXML_DIR);
        List<Path> fxmlFiles = Files.walk(fxmlFolder.toPath())
                .filter(p -> p.toString().endsWith(".fxml"))
                .collect(Collectors.toList());

        Pattern controllerPattern = Pattern.compile("fx:controller=\"([^\"]+)\"");
        Pattern fxIdPattern = Pattern.compile("fx:id=\"([^\"]+)\"");

        int checkedControllers = 0;

        for (Path fxmlPath : fxmlFiles) {
            String content = Files.readString(fxmlPath);
            Matcher cMatcher = controllerPattern.matcher(content);

            if (cMatcher.find()) {
                String controllerClassName = cMatcher.group(1);
                try {
                    Class<?> clazz = Class.forName(controllerClassName);
                    Matcher idMatcher = fxIdPattern.matcher(content);

                    while (idMatcher.find()) {
                        String id = idMatcher.group(1);
                        boolean fieldFound = hasField(clazz, id);
                        assertTrue(fieldFound,
                                "FXML " + fxmlPath.getFileName() + " binds fx:id='" + id + "' but field was not found in " + controllerClassName);
                    }
                    checkedControllers++;
                } catch (ClassNotFoundException e) {
                    fail("Could not find class: " + controllerClassName);
                }
            }
        }

        assertTrue(checkedControllers >= 40, "Should have verified at least 40 controller bindings");
    }

    @Test
    @DisplayName("Verify FXML onAction and onMouseClicked event handlers exist in controllers")
    void testFxmlActionHandlersExistInControllers() throws IOException {
        File fxmlFolder = new File(FXML_DIR);
        List<Path> fxmlFiles = Files.walk(fxmlFolder.toPath())
                .filter(p -> p.toString().endsWith(".fxml"))
                .collect(Collectors.toList());

        Pattern controllerPattern = Pattern.compile("fx:controller=\"([^\"]+)\"");
        Pattern handlerPattern = Pattern.compile("(?:onAction|onMouseClicked)=\"#([^\"]+)\"");

        for (Path fxmlPath : fxmlFiles) {
            String content = Files.readString(fxmlPath);
            Matcher cMatcher = controllerPattern.matcher(content);

            if (cMatcher.find()) {
                String controllerClassName = cMatcher.group(1);
                try {
                    Class<?> clazz = Class.forName(controllerClassName);
                    Matcher hMatcher = handlerPattern.matcher(content);

                    while (hMatcher.find()) {
                        String methodName = hMatcher.group(1);
                        boolean methodFound = hasMethod(clazz, methodName);
                        assertTrue(methodFound,
                                "FXML " + fxmlPath.getFileName() + " binds handler '#" + methodName + "' but method was not found in " + controllerClassName);
                    }
                } catch (ClassNotFoundException e) {
                    fail("Could not find class: " + controllerClassName);
                }
            }
        }
    }

    private boolean hasField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            for (Field f : current.getDeclaredFields()) {
                if (f.getName().equals(fieldName)) {
                    return true;
                }
            }
            current = current.getSuperclass();
        }
        return false;
    }

    private boolean hasMethod(Class<?> clazz, String methodName) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            for (Method m : current.getDeclaredMethods()) {
                if (m.getName().equals(methodName)) {
                    return true;
                }
            }
            current = current.getSuperclass();
        }
        return false;
    }
}

