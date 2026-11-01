package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.util.AlertHelper;
import com.example.car_rental_sys.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * Controller for the Fullscreen In-Cabin Driver Cockpit Navigation Mode.
 */
public class DrivingModeController extends Controller {

    @FXML
    public Pane mainPane;

    @FXML
    private ImageView userAvatar;

    @FXML
    private Pane sideIconLogo;

    @FXML
    private Pane sideIconHome;

    @FXML
    private Pane sideIconVolume;

    @FXML
    private Pane sideIconPhone;

    @FXML
    private Pane sideIconSetting;

    @FXML
    private Pane browserContainer;

    @FXML
    private Pane distanceDataPane;

    @FXML
    private Label distanceLeftLabel;

    @FXML
    private Label timeLeftLabel;

    @FXML
    public void initialize() {
        if (distanceLeftLabel != null) distanceLeftLabel.setText("14.8 km left");
        if (timeLeftLabel != null) timeLeftLabel.setText("18 mins");
    }

    @FXML
    private void userAvatarClicked(MouseEvent event) {
        SceneManager.switchScene("PersonalInfoPage.fxml");
    }

    @FXML
    private void backToMainPage(MouseEvent event) {
        SceneManager.switchScene("mainPage.fxml");
    }

    @FXML
    private void backToDriverMainPage(MouseEvent event) {
        SceneManager.switchScene("driverServicePage.fxml");
    }

    @FXML
    private void phoneIconClicked(MouseEvent event) {
        AlertHelper.showInfo("Fleet Dispatch", "Connecting to Fleet Operations Dispatch Center...");
    }

    @FXML
    private void backToSettingPage(MouseEvent event) {
        SceneManager.switchScene("SettingPage.fxml");
    }
}

