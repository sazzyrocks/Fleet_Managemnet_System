package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.concurrency.AsyncExecutor;
import com.example.car_rental_sys.model.Vehicle;
import com.example.car_rental_sys.model.VehicleStatus;
import com.example.car_rental_sys.service.ServiceFactory;
import com.example.car_rental_sys.service.VehicleService;
import com.example.car_rental_sys.util.AlertHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Controller for the Fleet Inventory Overview Table within the Admin Panel.
 */
public class OverviewVehiclePageController extends Controller {

    @FXML
    private ScrollPane scrollPaneVehicle;

    private VBox tableContainer;

    @FXML
    public void initialize() {
        tableContainer = new VBox(10);
        tableContainer.setPadding(new Insets(15));
        tableContainer.setStyle("-fx-background-color: white;");

        if (scrollPaneVehicle != null) {
            scrollPaneVehicle.setContent(tableContainer);
            scrollPaneVehicle.setFitToWidth(true);
            scrollPaneVehicle.setStyle("-fx-background-color: white; -fx-background: white; -fx-border-color: #e5e6eb;");
        }

        loadFleetVehicles();
    }

    private void loadFleetVehicles() {
        Label loading = new Label("Loading fleet inventory records...");
        loading.setStyle("-fx-text-fill: #86909c; -fx-font-size: 14px;");
        tableContainer.getChildren().setAll(loading);

        AsyncExecutor.getInstance().execute(() -> {
            try {
                VehicleService vehicleService = ServiceFactory.getInstance().getVehicleService();
                List<Vehicle> list = vehicleService.getAllVehicles();

                Platform.runLater(() -> renderFleetTable(list));
            } catch (Exception e) {
                Platform.runLater(() -> {
                    Label error = new Label("Failed to load inventory: " + e.getMessage());
                    error.setStyle("-fx-text-fill: #f53f3f; -fx-font-size: 14px;");
                    tableContainer.getChildren().setAll(error);
                });
            }
        });
    }

    private void renderFleetTable(List<Vehicle> vehicles) {
        tableContainer.getChildren().clear();

        // Table Header
        HBox header = new HBox(15);
        header.setPadding(new Insets(10, 12, 10, 12));
        header.setStyle("-fx-background-color: #f2f3f5; -fx-background-radius: 6px; -fx-font-weight: bold; -fx-font-size: 13px;");

        Label colId = new Label("ID");
        colId.setPrefWidth(40);
        Label colName = new Label("Vehicle");
        colName.setPrefWidth(220);
        Label colPlate = new Label("Plate No.");
        colPlate.setPrefWidth(120);
        Label colRate = new Label("Daily Rate");
        colRate.setPrefWidth(90);
        Label colStatus = new Label("Status");
        colStatus.setPrefWidth(100);
        Label colActions = new Label("Actions");
        HBox.setHgrow(colActions, Priority.ALWAYS);

        header.getChildren().addAll(colId, colName, colPlate, colRate, colStatus, colActions);
        tableContainer.getChildren().add(header);

        if (vehicles.isEmpty()) {
            Label empty = new Label("No vehicles registered in fleet.");
            empty.setPadding(new Insets(20));
            empty.setStyle("-fx-text-fill: #86909c; -fx-font-size: 14px;");
            tableContainer.getChildren().add(empty);
            return;
        }

        // Table Rows
        for (Vehicle v : vehicles) {
            HBox row = new HBox(15);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(8, 12, 8, 12));
            row.setStyle("-fx-border-color: #f2f3f5; -fx-border-width: 0 0 1 0;");

            Label lblId = new Label("#" + v.getVehicleId());
            lblId.setPrefWidth(40);
            lblId.setStyle("-fx-text-fill: #86909c;");

            Label lblName = new Label(v.getBrand() + " " + v.getModel());
            lblName.setPrefWidth(220);
            lblName.setStyle("-fx-font-weight: bold; -fx-text-fill: #1d2129;");

            Label lblPlate = new Label(v.getLicensePlate() != null ? v.getLicensePlate() : "N/A");
            lblPlate.setPrefWidth(120);
            lblPlate.setStyle("-fx-text-fill: #4e5969;");

            Label lblRate = new Label("$" + (v.getDailyRate() != null ? v.getDailyRate().toPlainString() : "0.00"));
            lblRate.setPrefWidth(90);
            lblRate.setStyle("-fx-text-fill: #165dff; -fx-font-weight: bold;");

            Label lblStatus = new Label(v.getStatus() != null ? v.getStatus().name() : "AVAILABLE");
            lblStatus.setPrefWidth(100);
            applyStatusBadgeStyle(lblStatus, v.getStatus());

            HBox actionBox = new HBox(8);
            actionBox.setAlignment(Pos.CENTER_LEFT);

            Button btnToggle = new Button(v.getStatus() == VehicleStatus.AVAILABLE ? "Set Maint." : "Set Avail.");
            btnToggle.setStyle("-fx-background-color: #f2f3f5; -fx-font-size: 11px; -fx-cursor: hand;");
            btnToggle.setOnAction(e -> toggleVehicleStatus(v));

            Button btnDelete = new Button("Delete");
            btnDelete.setStyle("-fx-background-color: #ffece8; -fx-text-fill: #f53f3f; -fx-font-size: 11px; -fx-cursor: hand;");
            btnDelete.setOnAction(e -> deleteVehicle(v));

            actionBox.getChildren().addAll(btnToggle, btnDelete);
            row.getChildren().addAll(lblId, lblName, lblPlate, lblRate, lblStatus, actionBox);
            tableContainer.getChildren().add(row);
        }
    }

    private void applyStatusBadgeStyle(Label badge, VehicleStatus status) {
        if (status == VehicleStatus.AVAILABLE) {
            badge.setStyle("-fx-text-fill: #00b42a; -fx-font-weight: bold;");
        } else if (status == VehicleStatus.RENTED) {
            badge.setStyle("-fx-text-fill: #ff7d00; -fx-font-weight: bold;");
        } else {
            badge.setStyle("-fx-text-fill: #86909c; -fx-font-weight: bold;");
        }
    }

    private void toggleVehicleStatus(Vehicle vehicle) {
        VehicleStatus newStatus = (vehicle.getStatus() == VehicleStatus.AVAILABLE)
                ? VehicleStatus.MAINTENANCE
                : VehicleStatus.AVAILABLE;

        AsyncExecutor.getInstance().execute(() -> {
            try {
                VehicleService vehicleService = ServiceFactory.getInstance().getVehicleService();
                vehicleService.updateStatus(vehicle.getVehicleId(), newStatus);
                Platform.runLater(this::loadFleetVehicles);
            } catch (Exception e) {
                Platform.runLater(() -> AlertHelper.showError("Status Update Failed", e.getMessage()));
            }
        });
    }

    private void deleteVehicle(Vehicle vehicle) {
        boolean confirmed = AlertHelper.showConfirmation(
                "Delete Vehicle",
                "Are you sure you want to delete " + vehicle.getBrand() + " " + vehicle.getModel() + "?"
        );
        if (!confirmed) return;

        AsyncExecutor.getInstance().execute(() -> {
            try {
                VehicleService vehicleService = ServiceFactory.getInstance().getVehicleService();
                vehicleService.deleteVehicle(vehicle.getVehicleId());
                Platform.runLater(this::loadFleetVehicles);
            } catch (Exception e) {
                Platform.runLater(() -> AlertHelper.showError("Delete Failed", e.getMessage()));
            }
        });
    }
}

