package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.concurrency.AsyncExecutor;
import com.example.car_rental_sys.dao.DAOFactory;
import com.example.car_rental_sys.dao.OrderDAO;
import com.example.car_rental_sys.model.Order;
import com.example.car_rental_sys.model.User;
import com.example.car_rental_sys.service.AuthService;
import com.example.car_rental_sys.service.ServiceFactory;
import com.example.car_rental_sys.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.List;

/**
 * Controller for the Customer Portal Sidebar.
 */
public class CustomerSideBarController extends Controller {

    @FXML
    private ImageView avatarImageView;

    @FXML
    private Label nameTextLabel;

    @FXML
    private Label emailTextLabel;

    @FXML
    private Pane item1; // Profile

    @FXML
    private Pane item2; // Message

    @FXML
    private Pane item3; // Wallet

    @FXML
    private Pane item4; // History

    @FXML
    private Pane item5; // Setting

    @FXML
    private Pane item6; // Logout

    @FXML
    private ImageView vipCardImageView;

    @FXML
    private Pane bar1;
    @FXML
    private Pane bar2;
    @FXML
    private Pane bar3;
    @FXML
    private Pane bar4;
    @FXML
    private Pane bar5;

    @FXML
    private Button customerSideBarUpdateBtn;

    @FXML
    private ImageView vipBadgeImageView;

    @FXML
    private Label orderNumLabel;

    @FXML
    public void initialize() {
        populateCustomerData();
        attachNavigationHandlers();
    }

    private void populateCustomerData() {
        AuthService authService = ServiceFactory.getInstance().getAuthService();
        User currentUser = authService.getCurrentUser();

        if (currentUser != null) {
            if (nameTextLabel != null) {
                nameTextLabel.setText(currentUser.getFullName() != null ? currentUser.getFullName() : currentUser.getUsername());
            }
            if (emailTextLabel != null) {
                emailTextLabel.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "customer@fleet.local");
            }

            // Load order history count asynchronously
            AsyncExecutor.getInstance().execute(() -> {
                try {
                    OrderDAO orderDAO = DAOFactory.getInstance().getOrderDAO();
                    List<Order> userOrders = orderDAO.findByUserId(currentUser.getUserId());
                    int count = userOrders.size();

                    Platform.runLater(() -> {
                        if (orderNumLabel != null) {
                            orderNumLabel.setText(String.format("%02d", count));
                        }
                    });
                } catch (Exception ignored) {
                }
            });
        }
    }

    private void attachNavigationHandlers() {
        if (item1 != null) {
            item1.setOnMouseClicked(event -> SceneManager.switchScene("PersonalInfoPage.fxml"));
        }
        if (item2 != null) {
            item2.setOnMouseClicked(event -> SceneManager.switchScene("messagePage.fxml"));
        }
        if (item3 != null) {
            item3.setOnMouseClicked(event -> SceneManager.switchScene("addBankCardPage.fxml"));
        }
        if (item4 != null) {
            item4.setOnMouseClicked(event -> SceneManager.switchScene("showOrderComponent.fxml"));
        }
        if (item5 != null) {
            item5.setOnMouseClicked(event -> SceneManager.switchScene("SettingPage.fxml"));
        }
        if (item6 != null) {
            item6.setOnMouseClicked(event -> handleLogout());
        }
    }

    @FXML
    private void updateBtnClick(MouseEvent event) {
        populateCustomerData();
    }

    private void handleLogout() {
        ServiceFactory.getInstance().getAuthService().logout();
        SceneManager.switchScene("loginPage.fxml");
    }
}

