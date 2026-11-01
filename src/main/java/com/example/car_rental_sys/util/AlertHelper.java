package com.example.car_rental_sys.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Modern JavaFX dialog and alert utility for displaying consistent notifications.
 */
public final class AlertHelper {

    private AlertHelper() {
        // Prevent instantiation
    }

    public static void showInfo(String title, String message) {
        showAlert(AlertType.INFORMATION, title, message);
    }

    public static void showWarning(String title, String message) {
        showAlert(AlertType.WARNING, title, message);
    }

    public static void showError(String title, String message) {
        showAlert(AlertType.ERROR, title, message);
    }

    public static void showSuccess(String title, String message) {
        showAlert(AlertType.INFORMATION, title != null ? title : "Success", message);
    }

    public static boolean showConfirmation(String title, String message) {
        if (!Platform.isFxApplicationThread()) {
            return false;
        }
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private static void showAlert(AlertType alertType, String title, String message) {
        if (Platform.isFxApplicationThread()) {
            displayAlert(alertType, title, message);
        } else {
            Platform.runLater(() -> displayAlert(alertType, title, message));
        }
    }

    private static void displayAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}

