package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.concurrency.AsyncExecutor;
import com.example.car_rental_sys.dao.DAOFactory;
import com.example.car_rental_sys.model.OrderStatus;
import com.example.car_rental_sys.model.VehicleStatus;
import com.example.car_rental_sys.service.ServiceFactory;
import com.example.car_rental_sys.service.VehicleService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;

/**
 * Controller for the Enterprise Analytics & Performance Dashboard.
 */
public class dashboardController extends Controller {

    @FXML
    private Pane browserPane;

    @FXML
    public void initialize() {
        renderKpiDashboard();
    }

    private void renderKpiDashboard() {
        if (browserPane == null) return;

        VBox root = new VBox(25);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f7f8fa;");
        root.setPrefSize(1000, 750);

        Label title = new Label("Fleet Operations & Financial Analytics");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #1d2129;");

        FlowPane cardGrid = new FlowPane();
        cardGrid.setHgap(20);
        cardGrid.setVgap(20);

        VBox cardFleet = createStatCard("Total Fleet Size", "...", "#165dff");
        VBox cardActive = createStatCard("Active Rentals", "...", "#00b42a");
        VBox cardRevenue = createStatCard("Total Revenue", "...", "#f7ba1e");
        VBox cardPending = createStatCard("Maintenance Required", "...", "#f53f3f");

        cardGrid.getChildren().addAll(cardFleet, cardActive, cardRevenue, cardPending);
        root.getChildren().addAll(title, cardGrid);
        browserPane.getChildren().setAll(root);

        // Fetch metrics asynchronously
        AsyncExecutor.getInstance().execute(() -> {
            try {
                VehicleService vehicleService = ServiceFactory.getInstance().getVehicleService();
                long totalVehicles = vehicleService.getAllVehicles().size();

                java.util.List<com.example.car_rental_sys.model.Order> confirmedOrders = 
                        DAOFactory.getInstance().getOrderDAO().findByStatus(OrderStatus.CONFIRMED);
                for (com.example.car_rental_sys.model.Order order : confirmedOrders) {
                    vehicleService.updateVehicleStatus(order.getVehicleId(), VehicleStatus.RENTED);
                }

                long rentedVehicles = Math.max((long) confirmedOrders.size(), vehicleService.getFleetCountByStatus(VehicleStatus.RENTED));
                long maintenance = vehicleService.getFleetCountByStatus(VehicleStatus.MAINTENANCE);

                BigDecimal revenue = ServiceFactory.getInstance().getPaymentService().getTotalRevenue();

                Platform.runLater(() -> {
                    updateCardValue(cardFleet, String.valueOf(totalVehicles) + " Vehicles");
                    updateCardValue(cardActive, String.valueOf(rentedVehicles) + " Active");
                    updateCardValue(cardRevenue, "$" + (revenue != null ? revenue.toPlainString() : "0.00"));
                    updateCardValue(cardPending, String.valueOf(maintenance) + " Units");
                });
            } catch (Exception ignored) {
            }
        });
    }

    private VBox createStatCard(String labelText, String initialValue, String accentColor) {
        VBox card = new VBox(10);
        card.setPrefSize(220, 130);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12px;" +
                "-fx-border-color: #e5e6eb;" +
                "-fx-border-radius: 12px;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);"
        );

        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-text-fill: #86909c; -fx-font-size: 13px; -fx-font-weight: bold;");

        Label val = new Label(initialValue);
        val.setStyle("-fx-text-fill: " + accentColor + "; -fx-font-size: 24px; -fx-font-weight: bold;");

        card.getChildren().addAll(lbl, val);
        return card;
    }

    private void updateCardValue(VBox card, String value) {
        if (card.getChildren().size() > 1 && card.getChildren().get(1) instanceof Label) {
            ((Label) card.getChildren().get(1)).setText(value);
        }
    }
}

