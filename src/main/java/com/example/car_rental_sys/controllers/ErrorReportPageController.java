package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.util.AlertHelper;
import com.example.car_rental_sys.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * Controller for Diagnostic Feedback and System Error Submissions.
 */
public class ErrorReportPageController extends Controller {

    @FXML
    public Pane mainPane;

    @FXML
    private Label tipLabel;

    @FXML
    private Button okBtn;

    @FXML
    private Button cancelBtn;

    @FXML
    private RadioButton radioBtn1;

    @FXML
    private RadioButton radioBtn2;

    @FXML
    private ImageView screenShotImage;

    @FXML
    private Label diagnosticLabel;

    @FXML
    private Pane dropPane;

    @FXML
    public void initialize() {
        if (radioBtn1 != null) radioBtn1.setSelected(true);
        if (radioBtn2 != null) radioBtn2.setSelected(false);
    }

    @FXML
    private void okBtnClick(MouseEvent event) {
        AlertHelper.showSuccess("Feedback Sent", "Thank you! Your diagnostic feedback has been sent to our engineering team.");
        SceneManager.switchScene("mainPage.fxml");
    }

    @FXML
    private void cancelBtnClick(MouseEvent event) {
        SceneManager.switchScene("mainPage.fxml");
    }

    @FXML
    private void screenShotImageClick(MouseEvent event) {
        // Screenshot preview
    }

    @FXML
    private void diagnosticLabelClick(MouseEvent event) {
        SceneManager.switchScene("diagnosticDataPage.fxml");
    }
}

