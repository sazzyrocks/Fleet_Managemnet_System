package com.example.car_rental_sys.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the Main Customer Service Shell & Order Activity Portal.
 */
public class CustomerServiceController extends Controller {

    private static final Logger LOGGER = Logger.getLogger(CustomerServiceController.class.getName());

    public static CustomerServiceController instance;

    @FXML
    public AnchorPane mainPane;

    @FXML
    private Pane centerContainer;

    @FXML
    private Pane rightContainer;

    @FXML
    private Pane hugeContainer;

    @FXML
    private Pane middlePanel;

    @FXML
    private Pane settingMainPanel;

    @FXML
    public void initialize() {
        instance = this;
        if (hugeContainer != null) {
            hugeContainer.setVisible(false);
        }

        loadSubComponent(centerContainer, "showOrderComponent.fxml");
        loadSubComponent(rightContainer, "OrderDetailsComponent.fxml");
    }

    public void showSettingsPanel(String fxmlName) {
        if (centerContainer != null) centerContainer.setVisible(false);
        if (rightContainer != null) rightContainer.setVisible(false);
        if (hugeContainer != null) {
            hugeContainer.setVisible(true);
            if (settingMainPanel != null) {
                loadSubComponent(settingMainPanel, fxmlName);
            }
        }
    }

    public void showSettingPage() {
        showSettingsPanel("SettingPage.fxml");
        loadSubComponent(middlePanel, "MiddleBar.fxml");
    }

    public void showAccSecurity() {
        showSettingsPanel("AccountSecurityPage.fxml");
        loadSubComponent(middlePanel, "MiddleBar.fxml");
    }

    public void showPasswordPage() {
        showSettingsPanel("PasswordPage.fxml");
        loadSubComponent(middlePanel, "MiddleBar.fxml");
    }

    public void showProfilePage() {
        showSettingsPanel("PersonalInfoPage.fxml");
        loadSubComponent(middlePanel, "InfoMiddleBar.fxml");
    }

    private void loadSubComponent(Pane container, String fxmlName) {
        if (container == null) return;
        try {
            URL resource = getClass().getResource("/com/example/car_rental_sys/fxml/" + fxmlName);
            if (resource != null) {
                Parent root = FXMLLoader.load(resource);
                container.getChildren().setAll(root);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load sub component: " + fxmlName, e);
        }
    }
}

