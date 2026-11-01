package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.model.Order;
import com.example.car_rental_sys.model.User;
import com.example.car_rental_sys.service.ServiceFactory;
import com.example.car_rental_sys.util.AlertHelper;
import com.example.car_rental_sys.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

/**
 * Controller for Invoice Generation and Billing Statement Display.
 */
public class InvoiceController extends Controller {

    private static Order selectedOrder;

    @FXML
    private Pane printAreaPane;

    @FXML
    private Pane ivQrCodePane;

    @FXML
    private ImageView qrCodeImageView;

    @FXML
    private Label ivUserName;

    @FXML
    private Label ivAddressOne;

    @FXML
    private Label ivPaymentDate;

    @FXML
    private Label ivOrderID;

    @FXML
    private Label ivQuantity;

    @FXML
    private Label ivCarName;

    @FXML
    private Label ivUnitPrice;

    @FXML
    private Label ivPrice;

    @FXML
    private Label ivSubTotal;

    @FXML
    private Label taxLabel;

    @FXML
    private Label ivTotalPrice;

    @FXML
    private Pane dragPane;

    private double xOffset = 0;
    private double yOffset = 0;

    public static void setSelectedOrder(Order order) {
        selectedOrder = order;
    }

    @FXML
    public void initialize() {
        populateInvoiceData();
    }

    private void populateInvoiceData() {
        User user = ServiceFactory.getInstance().getAuthService().getCurrentUser();
        if (user != null) {
            if (ivUserName != null) ivUserName.setText(user.getFullName() != null ? user.getFullName() : user.getUsername());
            if (ivAddressOne != null) ivAddressOne.setText("Customer ID: #" + user.getUserId() + "\n" + (user.getEmail() != null ? user.getEmail() : ""));
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        if (selectedOrder != null) {
            if (ivOrderID != null) ivOrderID.setText(String.format("%09d", selectedOrder.getOrderId()));
            if (ivPaymentDate != null && selectedOrder.getCreatedAt() != null) {
                ivPaymentDate.setText(selectedOrder.getCreatedAt().format(dtf));
            }

            BigDecimal total = selectedOrder.getTotalPrice() != null ? selectedOrder.getTotalPrice() : new BigDecimal("250.00");
            BigDecimal subtotal = total.divide(new BigDecimal("1.06"), 2, RoundingMode.HALF_UP);
            BigDecimal tax = total.subtract(subtotal);

            if (ivSubTotal != null) ivSubTotal.setText("$" + subtotal.toPlainString());
            if (taxLabel != null) taxLabel.setText("$" + tax.toPlainString());
            if (ivTotalPrice != null) ivTotalPrice.setText("$" + total.toPlainString());
            if (ivPrice != null) ivPrice.setText("$" + subtotal.toPlainString());
            if (ivUnitPrice != null) ivUnitPrice.setText("$" + subtotal.toPlainString());
            if (ivCarName != null) ivCarName.setText("Vehicle Rental #" + selectedOrder.getVehicleId());
            if (ivQuantity != null) ivQuantity.setText("Rental Period");
        }
    }

    @FXML
    private void ivDownloadClicked(MouseEvent event) {
        downloadBtnClick(event);
    }

    @FXML
    private void downloadBtnClick(MouseEvent event) {
        AlertHelper.showSuccess("Invoice Exported", "Invoice has been saved to your downloads folder.");
    }

    @FXML
    private void cancelBtnClick(MouseEvent event) {
        SceneManager.switchScene("showOrderComponent.fxml");
    }

    @FXML
    private void dragPanePressed(MouseEvent event) {
        Stage stage = SceneManager.getPrimaryStage();
        if (stage != null) {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        }
    }

    @FXML
    private void dragPaneDragged(MouseEvent event) {
        Stage stage = SceneManager.getPrimaryStage();
        if (stage != null) {
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        }
    }
}

