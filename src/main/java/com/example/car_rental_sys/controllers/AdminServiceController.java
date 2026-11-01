package com.example.car_rental_sys.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the Main Administrator Service Shell & Dashboard Portal.
 */
public class AdminServiceController extends Controller {

    private static final Logger LOGGER = Logger.getLogger(AdminServiceController.class.getName());

    public static AdminServiceController instance;

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
    private Pane dashboardContainer;

    @FXML
    private Pane toRightPane;

    @FXML
    private ImageView toRightBtn;

    @FXML
    public void initialize() {
        instance = this;
        if (dashboardContainer != null) {
            dashboardContainer.setVisible(true);
        }
        if (hugeContainer != null) {
            hugeContainer.setVisible(false);
        }

        if (toRightPane != null) {
            toRightPane.setOnMouseClicked(event -> toggleContainers());
        }
    }

    private void toggleContainers() {
        if (dashboardContainer != null && hugeContainer != null) {
            boolean showDash = !dashboardContainer.isVisible();
            dashboardContainer.setVisible(showDash);
            hugeContainer.setVisible(!showDash);
        }
    }

    public void showSettingsView(String fxmlName) {
        if (dashboardContainer != null) dashboardContainer.setVisible(false);
        if (hugeContainer != null) {
            hugeContainer.setVisible(true);
            if (settingMainPanel != null) {
                loadSubView(settingMainPanel, fxmlName);
            }
        }
    }

    public void showSettingPage() {
        showSettingsView("SettingPage.fxml");
        loadSubView(middlePanel, "MiddleBar.fxml");
    }

    public void showAccSecurity() {
        showSettingsView("AccountSecurityPage.fxml");
        loadSubView(middlePanel, "MiddleBar.fxml");
    }

    public void showPasswordPage() {
        showSettingsView("PasswordPage.fxml");
        loadSubView(middlePanel, "MiddleBar.fxml");
    }

    public void showProfilePage() {
        showSettingsView("PersonalInfoPage.fxml");
        loadSubView(middlePanel, "InfoMiddleBar.fxml");
    }

    private void loadSubView(Pane targetPane, String fxmlName) {
        try {
            URL resource = getClass().getResource("/com/example/car_rental_sys/fxml/" + fxmlName);
            if (resource != null) {
                Parent root = FXMLLoader.load(resource);
                targetPane.getChildren().setAll(root);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load sub-view: " + fxmlName, e);
        }
    }
}

