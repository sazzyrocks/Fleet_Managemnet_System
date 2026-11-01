package com.example.car_rental_sys.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

/**
 * Controller for General Application Preferences and Privacy Settings.
 */
public class SettingPageController extends Controller {

    @FXML
    private Pane settingPane;

    @FXML
    private Pane switchPaneOne;

    @FXML
    private Pane switchPaneTwo;

    @FXML
    private Pane switchPaneThree;

    private boolean stateOne = true;
    private boolean stateTwo = true;
    private boolean stateThree = false;

    @FXML
    public void initialize() {
        setupSwitch(switchPaneOne, stateOne, () -> stateOne = !stateOne);
        setupSwitch(switchPaneTwo, stateTwo, () -> stateTwo = !stateTwo);
        setupSwitch(switchPaneThree, stateThree, () -> stateThree = !stateThree);
    }

    private void setupSwitch(Pane switchPane, boolean initialActive, Runnable onToggle) {
        if (switchPane == null) return;
        updateSwitchVisual(switchPane, initialActive);

        switchPane.setOnMouseClicked(event -> {
            onToggle.run();
            // Check state and update visual
            boolean current = switchPane.getStyle().contains("#165dff");
            updateSwitchVisual(switchPane, !current);
        });
    }

    private void updateSwitchVisual(Pane switchPane, boolean active) {
        if (active) {
            switchPane.setStyle("-fx-background-color: #165dff; -fx-background-radius: 12px; -fx-cursor: hand;");
        } else {
            switchPane.setStyle("-fx-background-color: #c9cdd4; -fx-background-radius: 12px; -fx-cursor: hand;");
        }
    }
}

