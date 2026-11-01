package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.concurrency.AsyncExecutor;
import com.example.car_rental_sys.model.Order;
import com.example.car_rental_sys.model.Vehicle;
import com.example.car_rental_sys.service.ServiceFactory;
import com.example.car_rental_sys.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Controller for viewing specific Order Specification and Invoice Summary.
 */
public class OrderDetailsComponentController extends Controller {

    private static Order selectedOrder;

    @FXML
    private Pane panelOrderDetails;

    @FXML
    private Label orderNum;

    @FXML
    private Label orderDate;

    @FXML
    private ImageView carImg;

    @FXML
    private Label carName;

    @FXML
    private Label carPrice;

    @FXML
    private Label orderQty;

    @FXML
    private Label paymentType;

    @FXML
    private Label lblLocation;

    @FXML
    private Label orderTime;

    @FXML
    private Label approveTime;

    @FXML
    private Label paymentTime;

    @FXML
    private Label pickUpTime;

    @FXML
    private Label dueTime;

    @FXML
    private Label lblFeedback;

    @FXML
    private Label totalAmount;

    @FXML
    private Button btnTrackOrder;

    @FXML
    private Button invoiceBtn;

    @FXML
    private ImageView invoiceIcon;

    @FXML
    private ImageView trackIcon;

    public static void setSelectedOrder(Order order) {
        selectedOrder = order;
    }

    public static Order getSelectedOrder() {
        return selectedOrder;
    }

    @FXML
    public void initialize() {
        populateOrderData();
        attachButtonEvents();
    }

    private void populateOrderData() {
        if (selectedOrder == null) return;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (orderNum != null) orderNum.setText(String.format("%09d", selectedOrder.getOrderId()));
        if (orderDate != null && selectedOrder.getCreatedAt() != null) {
            orderDate.setText(selectedOrder.getCreatedAt().format(df));
        }

        if (paymentType != null) paymentType.setText("Credit Card / Digital Payment");
        if (lblLocation != null) {
            lblLocation.setText(selectedOrder.getPickupLocation() != null ? selectedOrder.getPickupLocation() : "Central Airport Hub");
        }

        if (totalAmount != null && selectedOrder.getTotalPrice() != null) {
            totalAmount.setText("$" + selectedOrder.getTotalPrice().toPlainString());
            if (carPrice != null) carPrice.setText("$" + selectedOrder.getTotalPrice().toPlainString());
        }

        if (orderTime != null && selectedOrder.getCreatedAt() != null) {
            orderTime.setText(selectedOrder.getCreatedAt().format(dtf));
        }
        if (approveTime != null && selectedOrder.getCreatedAt() != null) {
            approveTime.setText(selectedOrder.getCreatedAt().plusMinutes(5).format(dtf));
        }
        if (paymentTime != null && selectedOrder.getCreatedAt() != null) {
            paymentTime.setText(selectedOrder.getCreatedAt().plusMinutes(6).format(dtf));
        }
        if (pickUpTime != null && selectedOrder.getStartDate() != null) {
            pickUpTime.setText(selectedOrder.getStartDate().format(dtf));
        }
        if (dueTime != null && selectedOrder.getEndDate() != null) {
            dueTime.setText(selectedOrder.getEndDate().format(dtf));
        }

        // Fetch vehicle details asynchronously
        AsyncExecutor.getInstance().execute(() -> {
            try {
                Optional<Vehicle> vOpt = ServiceFactory.getInstance().getVehicleService().getVehicleById(selectedOrder.getVehicleId());
                Platform.runLater(() -> {
                    if (vOpt.isPresent()) {
                        Vehicle v = vOpt.get();
                        if (carName != null) carName.setText(v.getBrand() + " " + v.getModel());
                        loadCarImage(carImg, v.getImagePath());
                    } else {
                        if (carName != null) carName.setText("Fleet Vehicle #" + selectedOrder.getVehicleId());
                    }
                });
            } catch (Exception ignored) {
            }
        });
    }

    private void attachButtonEvents() {
        if (invoiceBtn != null) {
            invoiceBtn.setOnMouseClicked(event -> {
                InvoiceController.setSelectedOrder(selectedOrder);
                SceneManager.switchScene("Invoice.fxml");
            });
        }
        if (lblFeedback != null) {
            lblFeedback.setOnMouseClicked(event -> SceneManager.switchScene("ViewCommentsPage.fxml"));
        }
    }

    @FXML
    private void btnTrackOrderClicked(MouseEvent event) {
        TrackOrderController.setSelectedOrder(selectedOrder);
        SceneManager.switchScene("TrackOrderComponent.fxml");
    }

    private void loadCarImage(ImageView imageView, String path) {
        if (imageView == null) return;
        try (InputStream stream = getClass().getResourceAsStream("/com/example/car_rental_sys/image/cars/Huracan_EVI.png")) {
            if (stream != null) imageView.setImage(new Image(stream));
        } catch (Exception ignored) {
        }
    }
}

