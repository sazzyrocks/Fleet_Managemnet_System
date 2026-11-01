package com.example.car_rental_sys.util;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized Scene & Navigation Manager for the JavaFX Desktop Client.
 */
public final class SceneManager {

    private static final Logger LOGGER = Logger.getLogger(SceneManager.class.getName());
    private static final double DEFAULT_WIDTH = 1280.0;
    private static final double DEFAULT_HEIGHT = 832.0;
    private static final String FXML_BASE_PATH = "/com/example/car_rental_sys/fxml/";

    private static Stage primaryStage;

    private SceneManager() {
        // Utility class
    }

    public static synchronized void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static synchronized Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Navigates to the designated FXML scene on the JavaFX Application Thread.
     *
     * @param fxmlFileName name of the FXML file (e.g. "loginPage.fxml")
     */
    public static void switchScene(String fxmlFileName) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> switchScene(fxmlFileName));
            return;
        }

        try {
            String path = fxmlFileName.startsWith("/") ? fxmlFileName : FXML_BASE_PATH + fxmlFileName;
            URL resource = SceneManager.class.getResource(path);
            if (resource == null) {
                LOGGER.severe("Cannot find FXML resource: " + path);
                AlertHelper.showError("Navigation Error", "View file not found: " + fxmlFileName);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            if (primaryStage == null) {
                LOGGER.severe("Primary stage is not initialized in SceneManager.");
                return;
            }

            Scene scene = primaryStage.getScene();
            if (scene == null) {
                scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
                primaryStage.setScene(scene);
            } else {
                scene.setRoot(root);
            }

            // Adjust stage size if loading smaller dialogs (e.g. loadingPage.fxml)
            if ("loadingPage.fxml".equalsIgnoreCase(fxmlFileName)) {
                primaryStage.setWidth(640.0);
                primaryStage.setHeight(416.0);
            } else {
                primaryStage.setWidth(DEFAULT_WIDTH);
                primaryStage.setHeight(DEFAULT_HEIGHT);
            }

            centerStage();
            if (!primaryStage.isShowing()) {
                primaryStage.show();
            }

            LOGGER.info("Switched to scene: " + fxmlFileName);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load scene: " + fxmlFileName, e);
            AlertHelper.showError("Navigation Error", "Failed to load view: " + e.getMessage());
        }
    }

    /**
     * Centers the primary stage on screen.
     */
    public static void centerStage() {
        if (primaryStage != null) {
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setX((screenBounds.getWidth() - primaryStage.getWidth()) / 2);
            primaryStage.setY((screenBounds.getHeight() - primaryStage.getHeight()) / 2);
        }
    }

    /**
     * Configures the application icon on the primary stage.
     */
    public static void setAppIcon() {
        if (primaryStage != null) {
            try (InputStream iconStream = SceneManager.class.getResourceAsStream("/com/example/car_rental_sys/image/UI/logoIcon.png")) {
                if (iconStream != null) {
                    primaryStage.getIcons().add(new Image(iconStream));
                }
            } catch (Exception e) {
                LOGGER.fine("Could not load application window icon: " + e.getMessage());
            }
        }
    }
}

