package com.example.car_rental_sys.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 * Controller for FXML Layout Diagnostic and Harness Testing.
 */
public class TextController {

    @FXML
    private Pane mainPane;

    @FXML
    public void initialize() {
        if (mainPane != null) {
            Label testLabel = new Label("Diagnostic Test Harness Ready");
            testLabel.setStyle("-fx-text-fill: #555555; -fx-font-size: 14px; -fx-padding: 10px;");
            mainPane.getChildren().add(testLabel);
        }
    }

    @FXML
    private void buttonClick() {
        if (mainPane != null) {
            System.out.println("[TextController] Harness diagnostic button clicked.");
        }
    }
}

