package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.concurrency.AsyncExecutor;
import com.example.car_rental_sys.dao.DAOFactory;
import com.example.car_rental_sys.dao.DriverDAO;
import com.example.car_rental_sys.model.Driver;
import com.example.car_rental_sys.model.User;
import com.example.car_rental_sys.service.AuthService;
import com.example.car_rental_sys.service.ServiceFactory;
import com.example.car_rental_sys.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.Optional;

/**
 * Controller for the Driver Portal Sidebar.
 */
public class DriverSideController extends Controller {

    @FXML
    private ImageView avatarImageView;

    @FXML
    private Label nameTextLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label postLabel;

    @FXML
    private Label rating;

    @FXML
    private Label exp;

    @FXML
    private Label speed;

    @FXML
    private Pane item1; // Profile

    @FXML
    private Pane item2; // Message

    @FXML
    private Pane item3; // Workplace

    @FXML
    private Pane item4; // Setting

    @FXML
    private Pane item5; // Logout

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
    public void initialize() {
        populateDriverData();
        attachNavigationHandlers();
    }

    private void populateDriverData() {
        AuthService authService = ServiceFactory.getInstance().getAuthService();
        User currentUser = authService.getCurrentUser();

        if (currentUser != null) {
            String displayName = currentUser.getFullName() != null ? currentUser.getFullName() : currentUser.getUsername();
            if (nameTextLabel != null) nameTextLabel.setText(displayName);
            if (nameLabel != null) nameLabel.setText(displayName);
            if (postLabel != null) postLabel.setText("Fleet Driver");

            // Query driver telemetry asynchronously
            AsyncExecutor.getInstance().execute(() -> {
                try {
                    DriverDAO driverDAO = DAOFactory.getInstance().getDriverDAO();
                    Optional<Driver> driverOpt = driverDAO.findByUserId(currentUser.getUserId());

                    Platform.runLater(() -> {
                        if (driverOpt.isPresent()) {
                            Driver d = driverOpt.get();
                            if (rating != null) {
                                rating.setText(String.format("%.1f", d.getRating() != null ? d.getRating().doubleValue() : 5.0));
                            }
                            if (exp != null) {
                                exp.setText(String.valueOf(d.getExperienceYears() != null ? d.getExperienceYears() : 3));
                            }
                            if (speed != null) {
                                speed.setText("Active");
                            }
                        } else {
                            if (rating != null) rating.setText("5.0");
                            if (exp != null) exp.setText("1");
                            if (speed != null) speed.setText("Ready");
                        }
                    });
                } catch (Exception ignored) {
                }
            });
        }
    }

    private void attachNavigationHandlers() {
        if (item1 != null) {
            item1.setOnMouseClicked(event -> {
                if (DriverMainPageController.driverMainPageInstance != null) {
                    DriverMainPageController.driverMainPageInstance.showProfilePage();
                } else {
                    SceneManager.switchScene("PersonalInfoPage.fxml");
                }
            });
        }
        if (item2 != null) {
            item2.setOnMouseClicked(event -> SceneManager.switchScene("messagePage.fxml"));
        }
        if (item3 != null) {
            item3.setOnMouseClicked(event -> SceneManager.switchScene("drivingModePage.fxml"));
        }
        if (item4 != null) {
            item4.setOnMouseClicked(event -> {
                if (DriverMainPageController.driverMainPageInstance != null) {
                    DriverMainPageController.driverMainPageInstance.showSettingPage();
                } else {
                    SceneManager.switchScene("SettingPage.fxml");
                }
            });
        }
        if (item5 != null) {
            item5.setOnMouseClicked(event -> handleLogout());
        }
    }

    private void handleLogout() {
        ServiceFactory.getInstance().getAuthService().logout();
        SceneManager.switchScene("loginPage.fxml");
    }
}

