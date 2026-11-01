package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.util.SceneManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller for Network Connectivity Diagnostics and Reconnection Dialog.
 */
public class NetworkIssueController {

    @FXML
    private Button cancelBtn, okBtn;

    @FXML
    private Label tipLabel;

    private boolean isReconnectingPromptShown = false;

    @FXML
    public void cancelBtnClick() {
        if (isReconnectingPromptShown) {
            Stage stage = getStage();
            if (stage != null) {
                stage.close();
            }
        } else {
            isReconnectingPromptShown = true;
            if (tipLabel != null) {
                tipLabel.setText("Active vehicle reservations and telematics require an active connection. Retry reconnection?");
            }
        }
    }

    @FXML
    public void okBtnClick() {
        if (tipLabel != null) {
            tipLabel.setText("Re-establishing network connection, please wait...");
        }

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            Platform.runLater(() -> {
                SceneManager.switchScene("mainPage.fxml");
            });
        }));
        timeline.play();
    }

    private Stage getStage() {
        if (okBtn != null && okBtn.getScene() != null) {
            return (Stage) okBtn.getScene().getWindow();
        }
        if (cancelBtn != null && cancelBtn.getScene() != null) {
            return (Stage) cancelBtn.getScene().getWindow();
        }
        return null;
    }
}

