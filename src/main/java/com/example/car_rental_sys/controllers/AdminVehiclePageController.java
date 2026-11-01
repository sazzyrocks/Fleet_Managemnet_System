package com.example.car_rental_sys.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the Administrator Vehicle Management Container (tabbed between Overview and Add/Edit).
 */
public class AdminVehiclePageController extends Controller {

    private static final Logger LOGGER = Logger.getLogger(AdminVehiclePageController.class.getName());

    @FXML
    private Pane adminVehiclePane;

    @FXML
    private Button overviewBtn;

    @FXML
    private Button addVehicleBtn;

    @FXML
    private Button editVehicleBtn;

    @FXML
    private Pane AdVehicleMainPane;

    @FXML
    public void initialize() {
        loadSubView("OverviewVehiclePage.fxml");
    }

    @FXML
    private void overviewPageClicked(MouseEvent event) {
        highlightButton(overviewBtn);
        loadSubView("OverviewVehiclePage.fxml");
    }

    @FXML
    private void backToDashboardClicked(MouseEvent event) {
        com.example.car_rental_sys.util.SceneManager.switchScene("adminServicePage.fxml");
    }

    @FXML
    private void addVehiclePageClicked(MouseEvent event) {
        highlightButton(addVehicleBtn);
        EditVehiclePageController.setEditMode(false, null);
        loadSubView("EditVehiclePage.fxml");
    }

    public void openEditMode(com.example.car_rental_sys.model.Vehicle vehicle) {
        if (editVehicleBtn != null) {
            editVehicleBtn.setVisible(true);
            highlightButton(editVehicleBtn);
        }
        EditVehiclePageController.setEditMode(true, vehicle);
        loadSubView("EditVehiclePage.fxml");
    }

    private void highlightButton(Button activeBtn) {
        if (overviewBtn != null) overviewBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #86909c; -fx-font-weight: bold;");
        if (addVehicleBtn != null) addVehicleBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #86909c; -fx-font-weight: bold;");
        if (editVehicleBtn != null && editVehicleBtn.isVisible()) editVehicleBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #86909c; -fx-font-weight: bold;");

        if (activeBtn != null) {
            activeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #165dff; -fx-font-weight: bold;");
        }
    }

    private void loadSubView(String fxmlFileName) {
        if (AdVehicleMainPane == null) {
            return;
        }

        try {
            URL resource = getClass().getResource("/com/example/car_rental_sys/fxml/" + fxmlFileName);
            if (resource == null) {
                LOGGER.severe("Cannot find sub-view resource: " + fxmlFileName);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent content = loader.load();
            AdVehicleMainPane.getChildren().setAll(content);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load sub-view: " + fxmlFileName, e);
        }
    }
}

