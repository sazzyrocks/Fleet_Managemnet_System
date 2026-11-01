package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Controller for the Main Public Landing Page / Catalog Launchpad.
 */
public class MainPageController extends Controller {

    @FXML
    public Pane mainPane;

    @FXML
    private Label introLabel;

    @FXML
    private ImageView carImage;

    @FXML
    private Pane operationBar;

    @FXML
    private Button searchBtn;

    @FXML
    private Button openCatalogBtn;

    @FXML
    private Label locationBtn;

    @FXML
    private Label locationText;

    @FXML
    private Label pickUpDateBtn;

    @FXML
    private Label pickUpDateLabel;

    @FXML
    private Label returnDateBtn;

    @FXML
    private Label returnDateLabel;

    private int locationIndex = 0;
    private final String[] locations = {
            "Kuala Lumpur, Malaysia",
            "Singapore Changi Hub",
            "Bangkok Central Station",
            "Jakarta Downtown Plaza"
    };

    @FXML
    public void initialize() {
        if (introLabel != null) {
            introLabel.setText("Experience premier luxury & economy vehicles with instant booking, zero hidden fees, and 24/7 dedicated fleet support.");
            introLabel.setWrapText(true);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        LocalDate today = LocalDate.now();
        if (pickUpDateLabel != null) {
            pickUpDateLabel.setText(today.format(formatter) + " 10:00 AM");
        }
        if (returnDateLabel != null) {
            returnDateLabel.setText(today.plusDays(7).format(formatter) + " 10:00 AM");
        }
    }

    @FXML
    private void searchBtnClick(ActionEvent event) {
        SceneManager.switchScene("carsListPage.fxml");
    }

    @FXML
    private void openCatalogBtnClick(ActionEvent event) {
        SceneManager.switchScene("carsListPage.fxml");
    }

    @FXML
    private void locationBtnClick(MouseEvent event) {
        locationIndex = (locationIndex + 1) % locations.length;
        if (locationText != null) {
            locationText.setText(locations[locationIndex]);
        }
    }

    @FXML
    private void pickUpBtnClick(MouseEvent event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        LocalDate date = LocalDate.now();
        if (pickUpDateLabel != null) {
            pickUpDateLabel.setText(date.format(formatter) + " 09:00 AM");
        }
    }

    @FXML
    private void returnBtnClick(MouseEvent event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        LocalDate date = LocalDate.now().plusDays(5);
        if (returnDateLabel != null) {
            returnDateLabel.setText(date.format(formatter) + " 06:00 PM");
        }
    }
}

