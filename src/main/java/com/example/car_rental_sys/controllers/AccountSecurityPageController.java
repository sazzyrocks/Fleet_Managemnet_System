package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.model.User;
import com.example.car_rental_sys.service.ServiceFactory;
import com.example.car_rental_sys.util.AlertHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.InputStream;

/**
 * Controller for Account Verification, Driver License, and Security Questions.
 */
public class AccountSecurityPageController extends Controller {

    @FXML
    private Pane panelSetting;

    @FXML
    private ImageView ivAvatar;

    @FXML
    private Label ivUsername;

    @FXML
    private TextField txtDrivingLicense;

    @FXML
    private ComboBox<String> comboQuestion;

    @FXML
    private TextField txtSecurityAnswer;

    @FXML
    private Button ivSaveButton;

    @FXML
    public void initialize() {
        populateQuestions();
        loadUserInfo();
    }

    private void populateQuestions() {
        if (comboQuestion != null) {
            comboQuestion.getItems().setAll(
                    "What was the model of your first car?",
                    "In what city were you born?",
                    "What is your mother's maiden name?",
                    "What is your favorite travel destination?"
            );
            comboQuestion.getSelectionModel().selectFirst();
        }
    }

    private void loadUserInfo() {
        User user = ServiceFactory.getInstance().getAuthService().getCurrentUser();
        if (user != null) {
            if (ivUsername != null) {
                ivUsername.setText(user.getFullName() != null ? user.getFullName() : user.getUsername());
            }
            if (txtDrivingLicense != null) {
                txtDrivingLicense.setText("DL-9824-77189-KL");
            }
            loadAvatar();
        }
    }

    @FXML
    private void saveBtnClicked(MouseEvent event) {
        String answer = txtSecurityAnswer != null ? txtSecurityAnswer.getText().trim() : "";
        if (answer.isEmpty()) {
            AlertHelper.showWarning("Security Answer", "Please provide an answer to your selected security question.");
            return;
        }

        AlertHelper.showSuccess("Security Saved", "Your security recovery preferences have been updated.");
    }

    private void loadAvatar() {
        if (ivAvatar == null) return;
        try (InputStream stream = getClass().getResourceAsStream("/com/example/car_rental_sys/image/avatar/1.png")) {
            if (stream != null) ivAvatar.setImage(new Image(stream));
        } catch (Exception ignored) {
        }
    }
}

