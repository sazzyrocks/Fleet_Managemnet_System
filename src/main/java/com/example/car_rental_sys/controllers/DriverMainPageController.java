package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.util.AlertHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;

/**
 * Controller for the Driver Mission Control & Active Trip Operations.
 */
public class DriverMainPageController extends Controller {

    public static DriverMainPageController driverMainPageInstance;

    @FXML
    public Pane mainPane;

    @FXML
    private Pane optionPane;

    @FXML
    private Pane middlePanel;

    @FXML
    private Pane settingMainPanel;

    @FXML
    private Pane workplacePane;

    @FXML
    private WebView webview;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Pane processTipPane;

    @FXML
    private Pane processImagePane;

    @FXML
    private Label processTipLabel;

    @FXML
    private Pane sidePane;

    @FXML
    private ImageView renterAvatar;

    @FXML
    private Label renterName;

    @FXML
    private Label renterPost;

    @FXML
    private Pane messagePane;

    @FXML
    private Pane phonePane;

    @FXML
    private Pane closeSideBarPane;

    @FXML
    private ImageView carImageView;

    @FXML
    private Label seatsLabel;

    @FXML
    private Label powerLabel;

    @FXML
    private Label speedLabel;

    @FXML
    private Label startLabel;

    @FXML
    private Label destinationLabel;

    @FXML
    private Label carNumberLabel;

    @FXML
    public void initialize() {
        driverMainPageInstance = this;
        populateActiveTrip();
    }

    private void populateActiveTrip() {
        if (renterName != null) renterName.setText("Alexander Wright");
        if (renterPost != null) renterPost.setText("Gold VIP Client");
        if (carNumberLabel != null) carNumberLabel.setText("WC 8888 D");
        if (startLabel != null) startLabel.setText("KLIA International Terminal");
        if (destinationLabel != null) destinationLabel.setText("Grand Hyatt Hotel KL");
        if (seatsLabel != null) seatsLabel.setText("4 Seats");
        if (powerLabel != null) powerLabel.setText("6.2 L V8");
        if (speedLabel != null) speedLabel.setText("330 km/h");
        if (processTipLabel != null) processTipLabel.setText("Trip in progress: Heading to Pickup");
    }

    @FXML
    private void phoneClick(MouseEvent event) {
        AlertHelper.showInfo("Client Contact", "Customer Phone: +60 12-345 6789\nConnecting call via secure relay...");
    }

    @FXML
    private void closeSideBarPaneClick(MouseEvent event) {
        if (sidePane != null) {
            sidePane.setVisible(!sidePane.isVisible());
        }
    }
}

