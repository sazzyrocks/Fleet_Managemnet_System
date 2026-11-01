package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.model.User;
import com.example.car_rental_sys.service.AuthService;
import com.example.car_rental_sys.service.ServiceFactory;
import com.example.car_rental_sys.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Controller for the Administrator Portal Sidebar.
 */
public class AdminSideBarController extends Controller {

    @FXML
    private ImageView avatarImageView;

    @FXML
    private Label postLabel;

    @FXML
    private Label nameTextLabel;

    @FXML
    private Label emailTextLabel;

    @FXML
    private Pane item1; // Profile

    @FXML
    private Pane item2; // Message

    @FXML
    private Pane item3; // Dashboard

    @FXML
    private Pane item4; // Cars

    @FXML
    private Pane item5; // History

    @FXML
    private Pane item6; // Setting

    @FXML
    private Pane item7; // Logout

    @FXML
    private ImageView imgIcon1;
    @FXML
    private ImageView imgIcon2;
    @FXML
    private ImageView imgIcon3;
    @FXML
    private ImageView imgIcon4;
    @FXML
    private ImageView imgIcon5;
    @FXML
    private ImageView imgIcon6;
    @FXML
    private ImageView imgIcon7;

    @FXML
    public void initialize() {
        populateUserInfo();
        attachNavigationHandlers();
    }

    private void populateUserInfo() {
        AuthService authService = ServiceFactory.getInstance().getAuthService();
        User currentUser = authService.getCurrentUser();

        if (currentUser != null) {
            if (nameTextLabel != null) {
                nameTextLabel.setText(currentUser.getFullName() != null ? currentUser.getFullName() : currentUser.getUsername());
            }
            if (emailTextLabel != null) {
                emailTextLabel.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "admin@fleet.local");
            }
            if (postLabel != null) {
                postLabel.setText("Fleet Administrator");
            }
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
            item3.setOnMouseClicked(event -> SceneManager.switchScene("adminServicePage.fxml"));
        }
        if (item4 != null) {
            item4.setOnMouseClicked(event -> SceneManager.switchScene("AdminVehiclePage.fxml"));
        }
        if (item5 != null) {
            item5.setOnMouseClicked(event -> SceneManager.switchScene("showOrderComponent.fxml"));
        }
        if (item6 != null) {
            item6.setOnMouseClicked(event -> SceneManager.switchScene("SettingPage.fxml"));
        }
        if (item7 != null) {
            item7.setOnMouseClicked(event -> handleLogout());
        }
    }

    private void handleLogout() {
        ServiceFactory.getInstance().getAuthService().logout();
        SceneManager.switchScene("loginPage.fxml");
    }
}

