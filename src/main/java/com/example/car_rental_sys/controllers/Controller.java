package com.example.car_rental_sys.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

/**
 * Base Controller class for all JavaFX views.
 */
public abstract class Controller {

    @FXML
    public Pane mainPane;

    public Pane getMainPane() {
        return mainPane;
    }
}

