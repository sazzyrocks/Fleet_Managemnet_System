package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.model.Role;
import com.example.car_rental_sys.model.User;
import com.example.car_rental_sys.service.ServiceFactory;
import com.example.car_rental_sys.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * Controller for General Application Preferences and Privacy Settings.
 */
public class SettingPageController extends Controller {

    @FXML
    private Pane settingPane;

    @FXML
    private Button backBtn;

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

    @FXML
    private void backToDashboardClicked(MouseEvent event) {
        User currentUser = ServiceFactory.getInstance().getAuthService().getCurrentUser();
        if (currentUser != null && currentUser.getRole() != null) {
            switch (currentUser.getRole()) {
                case ADMIN:
                    SceneManager.switchScene("adminServicePage.fxml");
                    return;
                case DRIVER:
                    SceneManager.switchScene("driverServicePage.fxml");
                    return;
                case CUSTOMER:
                default:
                    SceneManager.switchScene("customerServicePage.fxml");
                    return;
            }
        }
        SceneManager.switchScene("customerServicePage.fxml");
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

