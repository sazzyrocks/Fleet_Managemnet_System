package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.concurrency.AsyncExecutor;
import com.example.car_rental_sys.dao.DAOFactory;
import com.example.car_rental_sys.model.Payment;
import com.example.car_rental_sys.model.PaymentStatus;
import com.example.car_rental_sys.model.User;
import com.example.car_rental_sys.service.ServiceFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for Customer Billing Overview, Wallet, and Payment Transactions.
 */
public class BillingComponentController extends Controller {

    @FXML
    private ScrollPane billCardList;

    @FXML
    private Pane billCardDetailBox;

    @FXML
    private Button billAllBtn;

    @FXML
    private Button billCompleteBtn;

    @FXML
    private Button billPendingBtn;

    @FXML
    private Button billCancelBtn;

    @FXML
    private Pane cusBillTable;

    @FXML
    private Pane pagContainer;

    private List<Payment> allPayments = new ArrayList<>();
    private VBox tableRows;

    @FXML
    public void initialize() {
        setupTable();
        loadPayments();
    }

    private void setupTable() {
        if (cusBillTable != null) {
            tableRows = new VBox(6);
            tableRows.setPadding(new Insets(5));
            ScrollPane scrollPane = new ScrollPane(tableRows);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefSize(470, 140);
            scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
            cusBillTable.getChildren().setAll(scrollPane);
        }
    }

    private void loadPayments() {
        AsyncExecutor.getInstance().execute(() -> {
            try {
                List<Payment> list = DAOFactory.getInstance().getPaymentDAO().findAll();
                allPayments = list;
                Platform.runLater(() -> filterAndRender(null));
            } catch (Exception ignored) {
            }
        });
    }

    private void filterAndRender(PaymentStatus filterStatus) {
        if (tableRows == null) return;
        tableRows.getChildren().clear();

        List<Payment> displayList = allPayments;
        if (filterStatus != null) {
            displayList = allPayments.stream()
                    .filter(p -> p.getStatus() == filterStatus)
                    .collect(Collectors.toList());
        }

        if (displayList.isEmpty()) {
            Label empty = new Label("No recent payment activities.");
            empty.setStyle("-fx-text-fill: #86909c; -fx-font-size: 12px;");
            tableRows.getChildren().add(empty);
            return;
        }

        DateTimeFormatter df = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        for (Payment p : displayList) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(6, 8, 6, 8));
            row.setStyle("-fx-background-color: #1a1a24; -fx-background-radius: 6px;");

            Label idLbl = new Label("Txn #" + p.getPaymentId());
            idLbl.setPrefWidth(80);
            idLbl.setStyle("-fx-text-fill: #6aa1ff; -fx-font-weight: bold; -fx-font-size: 11px;");

            Label methodLbl = new Label(p.getPaymentMethod() != null ? p.getPaymentMethod() : "Card");
            methodLbl.setPrefWidth(90);
            methodLbl.setStyle("-fx-text-fill: #dfdfdf; -fx-font-size: 11px;");

            String dateStr = p.getPaymentDate() != null ? p.getPaymentDate().format(df) : "Recent";
            Label dateLbl = new Label(dateStr);
            dateLbl.setPrefWidth(95);
            dateLbl.setStyle("-fx-text-fill: #86909c; -fx-font-size: 11px;");

            Label amountLbl = new Label("$" + (p.getAmount() != null ? p.getAmount().toPlainString() : "0.00"));
            amountLbl.setPrefWidth(75);
            amountLbl.setStyle("-fx-text-fill: #00b42a; -fx-font-weight: bold; -fx-font-size: 11px;");

            Label statusLbl = new Label(p.getStatus() != null ? p.getStatus().name() : "PAID");
            HBox.setHgrow(statusLbl, Priority.ALWAYS);
            statusLbl.setStyle("-fx-text-fill: #dfdfdf; -fx-font-size: 11px;");

            row.getChildren().addAll(idLbl, methodLbl, dateLbl, amountLbl, statusLbl);
            tableRows.getChildren().add(row);
        }
    }

    @FXML
    private void billAllBtnClicked(MouseEvent event) {
        filterAndRender(null);
    }

    @FXML
    private void billCompleteBtnClicked(MouseEvent event) {
        filterAndRender(PaymentStatus.COMPLETED);
    }

    @FXML
    private void billPendingBtnClicked(MouseEvent event) {
        filterAndRender(PaymentStatus.PENDING);
    }

    @FXML
    private void billCancelBtnClicked(MouseEvent event) {
        filterAndRender(PaymentStatus.FAILED);
    }
}

