package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.concurrency.AsyncExecutor;
import com.example.car_rental_sys.dao.DAOFactory;
import com.example.car_rental_sys.dao.OrderDAO;
import com.example.car_rental_sys.model.Order;
import com.example.car_rental_sys.model.OrderStatus;
import com.example.car_rental_sys.model.User;
import com.example.car_rental_sys.service.AuthService;
import com.example.car_rental_sys.service.ServiceFactory;
import com.example.car_rental_sys.util.SceneManager;
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
 * Controller for the Order History & Management List Component.
 */
public class OrderListComponentController extends Controller {

    @FXML
    private Pane panelOrderList;

    @FXML
    private Label numOrder;

    @FXML
    private Button btnAllOrder;

    @FXML
    private Button btnComplete;

    @FXML
    private Button btnContinue;

    @FXML
    private Button btnCancel;

    @FXML
    private Pane tableContainer;

    @FXML
    private Pane pagContainer;

    @FXML
    private Pane paneFilterBox;

    private List<Order> allOrders = new ArrayList<>();
    private VBox orderRowsBox;

    @FXML
    public void initialize() {
        setupTable();
        loadOrders();
    }

    private void setupTable() {
        if (tableContainer != null) {
            orderRowsBox = new VBox(8);
            orderRowsBox.setPadding(new Insets(5));
            ScrollPane scrollPane = new ScrollPane(orderRowsBox);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefSize(460, 360);
            scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
            tableContainer.getChildren().setAll(scrollPane);
        }
    }

    private void loadOrders() {
        AsyncExecutor.getInstance().execute(() -> {
            try {
                AuthService authService = ServiceFactory.getInstance().getAuthService();
                User user = authService.getCurrentUser();
                OrderDAO orderDAO = DAOFactory.getInstance().getOrderDAO();

                List<Order> orders;
                if (user != null && user.isAdmin()) {
                    orders = orderDAO.findAll();
                } else if (user != null) {
                    orders = orderDAO.findByUserId(user.getUserId());
                } else {
                    orders = orderDAO.findAll();
                }

                allOrders = orders;
                Platform.runLater(() -> {
                    updateOrderCount(allOrders.size());
                    filterAndRender(null);
                });
            } catch (Exception ignored) {
            }
        });
    }

    private void filterAndRender(OrderStatus filterStatus) {
        if (orderRowsBox == null) return;
        orderRowsBox.getChildren().clear();

        List<Order> displayList = allOrders;
        if (filterStatus != null) {
            displayList = allOrders.stream()
                    .filter(o -> o.getStatus() == filterStatus)
                    .collect(Collectors.toList());
        }

        if (displayList.isEmpty()) {
            Label empty = new Label("No orders found for this status.");
            empty.setStyle("-fx-text-fill: #86909c; -fx-font-size: 13px; -fx-padding: 20px;");
            orderRowsBox.getChildren().add(empty);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Order order : displayList) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10, 8, 10, 8));
            row.setStyle("-fx-background-color: #f7f8fa; -fx-background-radius: 6px; -fx-cursor: hand;");

            Label idLbl = new Label("#" + order.getOrderId());
            idLbl.setPrefWidth(55);
            idLbl.setStyle("-fx-text-fill: #4080ff; -fx-font-weight: bold; -fx-font-size: 12px;");

            Label vehicleLbl = new Label("Vehicle #" + order.getVehicleId());
            vehicleLbl.setPrefWidth(100);
            vehicleLbl.setStyle("-fx-text-fill: #4e5969; -fx-font-size: 12px; -fx-font-weight: bold;");

            String dateStr = order.getStartDate() != null ? order.getStartDate().format(formatter) : "N/A";
            Label dateLbl = new Label(dateStr);
            dateLbl.setPrefWidth(85);
            dateLbl.setStyle("-fx-text-fill: #86909c; -fx-font-size: 11px;");

            Label priceLbl = new Label("$" + (order.getTotalAmount() != null ? order.getTotalAmount().toPlainString() : "0.00"));
            priceLbl.setPrefWidth(80);
            priceLbl.setStyle("-fx-text-fill: #1d2129; -fx-font-weight: bold; -fx-font-size: 12px;");

            Label statusLbl = new Label(order.getStatus() != null ? order.getStatus().name() : "PENDING");
            HBox.setHgrow(statusLbl, Priority.ALWAYS);
            applyStatusStyle(statusLbl, order.getStatus());

            row.getChildren().addAll(idLbl, vehicleLbl, dateLbl, priceLbl, statusLbl);
            row.setOnMouseClicked(e -> {
                OrderDetailsComponentController.setSelectedOrder(order);
                SceneManager.switchScene("OrderDetailsComponent.fxml");
            });

            orderRowsBox.getChildren().add(row);
        }
    }

    private void applyStatusStyle(Label badge, OrderStatus status) {
        if (status == OrderStatus.COMPLETED) {
            badge.setStyle("-fx-text-fill: #00b42a; -fx-font-weight: bold; -fx-font-size: 11px;");
        } else if (status == OrderStatus.CONFIRMED || status == OrderStatus.ACTIVE) {
            badge.setStyle("-fx-text-fill: #165dff; -fx-font-weight: bold; -fx-font-size: 11px;");
        } else if (status == OrderStatus.CANCELLED) {
            badge.setStyle("-fx-text-fill: #f53f3f; -fx-font-weight: bold; -fx-font-size: 11px;");
        } else {
            badge.setStyle("-fx-text-fill: #ff7d00; -fx-font-weight: bold; -fx-font-size: 11px;");
        }
    }

    private void updateOrderCount(int count) {
        if (numOrder != null) {
            numOrder.setText(count + " order" + (count == 1 ? "" : "s"));
        }
    }

    @FXML
    private void btnAllOrderClicked(MouseEvent event) {
        highlightActiveButton(btnAllOrder);
        filterAndRender(null);
    }

    @FXML
    private void btnCompletedClicked(MouseEvent event) {
        highlightActiveButton(btnComplete);
        filterAndRender(OrderStatus.COMPLETED);
    }

    @FXML
    private void btnContinueClicked(MouseEvent event) {
        highlightActiveButton(btnContinue);
        filterAndRender(OrderStatus.CONFIRMED);
    }

    @FXML
    private void btnCancelClicked(MouseEvent event) {
        highlightActiveButton(btnCancel);
        filterAndRender(OrderStatus.CANCELLED);
    }

    private void highlightActiveButton(Button active) {
        Button[] btns = {btnAllOrder, btnComplete, btnContinue, btnCancel};
        for (Button b : btns) {
            if (b != null) {
                b.setStyle("-fx-background-color: transparent; -fx-text-fill: #86909c; -fx-font-weight: bold;");
            }
        }
        if (active != null) {
            active.setStyle("-fx-background-color: transparent; -fx-text-fill: #4080ff; -fx-font-weight: bold;");
        }
    }
}

