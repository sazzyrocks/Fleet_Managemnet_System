package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.concurrency.AsyncExecutor;
import com.example.car_rental_sys.model.Order;
import com.example.car_rental_sys.service.ServiceFactory;
import com.example.car_rental_sys.util.AlertHelper;
import com.example.car_rental_sys.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.time.format.DateTimeFormatter;

/**
 * Controller for Live Order Tracking and Vehicle Pickup/Delivery Status.
 */
public class TrackOrderController extends Controller {

    private static Order selectedOrder;

    @FXML
    private Pane panelTracking;

    @FXML
    private Pane mapTracking;

    @FXML
    private Label lblOrderID;

    @FXML
    private ImageView leftIcon;

    @FXML
    private Button btnReceive;

    @FXML
    private Label lblLocation;

    @FXML
    private Label lblPickupTime;

    @FXML
    private Label lblPickupDate;

    public static void setSelectedOrder(Order order) {
        selectedOrder = order;
    }

    @FXML
    public void initialize() {
        populateTrackingInfo();
        attachButtonEvents();
    }

    private void populateTrackingInfo() {
        if (selectedOrder != null) {
            if (lblOrderID != null) {
                lblOrderID.setText("ID #" + selectedOrder.getOrderId());
            }
            if (lblLocation != null) {
                lblLocation.setText(selectedOrder.getPickupLocation() != null ? selectedOrder.getPickupLocation() : "Central Airport Fleet Terminal");
            }

            DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            if (lblPickupDate != null && selectedOrder.getStartDate() != null) {
                lblPickupDate.setText(selectedOrder.getStartDate().format(df));
            }
            if (lblPickupTime != null && selectedOrder.getStartDate() != null) {
                lblPickupTime.setText(selectedOrder.getStartDate().format(tf) + " - " + selectedOrder.getStartDate().plusHours(1).format(tf));
            }
        }
    }

    private void attachButtonEvents() {
        if (btnReceive != null) {
            btnReceive.setOnAction(e -> handleOrderReceived());
        }
    }

    private void handleOrderReceived() {
        if (selectedOrder == null) return;

        btnReceive.setDisable(true);
        btnReceive.setText("Confirming...");

        AsyncExecutor.getInstance().execute(() -> {
            try {
                ServiceFactory.getInstance().getRentalService().completeBooking(selectedOrder.getOrderId());
                Platform.runLater(() -> {
                    btnReceive.setText("Received");
                    AlertHelper.showSuccess("Trip Completed", "Vehicle handover confirmed. Thank you for choosing our fleet service!");
                    SceneManager.switchScene("showOrderComponent.fxml");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    btnReceive.setDisable(false);
                    btnReceive.setText("Order Received");
                    AlertHelper.showError("Confirmation Failed", e.getMessage());
                });
            }
        });
    }

    @FXML
    private void leftIconClicked(MouseEvent event) {
        SceneManager.switchScene("showOrderComponent.fxml");
    }
}

