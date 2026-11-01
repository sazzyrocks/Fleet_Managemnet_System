package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.concurrency.AsyncExecutor;
import com.example.car_rental_sys.service.ServiceFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.math.BigDecimal;

/**
 * Controller for the Billing Summary and Spending Analytics sidebar.
 */
public class BillingSideBarController extends Controller {

    @FXML
    private Label billSideText;

    @FXML
    private Pane lineChart;

    @FXML
    private Pane radioChart;

    @FXML
    public void initialize() {
        loadTotalSpending();
    }

    private void loadTotalSpending() {
        AsyncExecutor.getInstance().execute(() -> {
            try {
                BigDecimal total = ServiceFactory.getInstance().getPaymentService().getTotalRevenue();
                Platform.runLater(() -> {
                    if (billSideText != null) {
                        billSideText.setText("$" + (total != null ? total.toPlainString() : "0.00"));
                    }
                });
            } catch (Exception ignored) {
            }
        });
    }
}

