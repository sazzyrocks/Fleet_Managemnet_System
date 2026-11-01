package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.concurrency.AsyncExecutor;
import com.example.car_rental_sys.dao.DAOFactory;
import com.example.car_rental_sys.model.User;
import com.example.car_rental_sys.service.ServiceFactory;
import com.example.car_rental_sys.util.AlertHelper;
import com.example.car_rental_sys.util.PasswordUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.InputStream;

/**
 * Controller for Password Modification & Security Credentials.
 */
public class PasswordPageController extends Controller {

    @FXML
    private Pane panelSettings;

    @FXML
    private ImageView ivAvatar;

    @FXML
    private Label ivUsername;

    @FXML
    private TextField txtOldPwd;

    @FXML
    private TextField txtNewPwd;

    @FXML
    private TextField txtReEnterPwd;

    @FXML
    private Button btnChangePwd;

    @FXML
    private ImageView imgOldPwd;

    @FXML
    private Label lblPasswordLen;

    @FXML
    private Label lblPasswordTemplate;

    @FXML
    private Label lblPwdNotice;

    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = ServiceFactory.getInstance().getAuthService().getCurrentUser();
        loadUserInfo();
    }

    private void loadUserInfo() {
        if (currentUser != null) {
            if (ivUsername != null) {
                ivUsername.setText(currentUser.getFullName() != null ? currentUser.getFullName() : currentUser.getUsername());
            }
            if (txtOldPwd != null) {
                txtOldPwd.setEditable(true);
                txtOldPwd.setPromptText("Enter current password");
            }
            loadAvatar();
        }
    }

    @FXML
    private void changePasswordClicked(MouseEvent event) {
        if (currentUser == null) return;

        String oldPwd = txtOldPwd != null ? txtOldPwd.getText() : "";
        String newPwd = txtNewPwd != null ? txtNewPwd.getText() : "";
        String reEnter = txtReEnterPwd != null ? txtReEnterPwd.getText() : "";

        if (oldPwd.isEmpty() || newPwd.isEmpty() || reEnter.isEmpty()) {
            AlertHelper.showWarning("Input Required", "Please fill in all password fields.");
            return;
        }

        if (!PasswordUtil.verifyPassword(oldPwd, currentUser.getPasswordHash())) {
            AlertHelper.showWarning("Incorrect Password", "The current password you entered is incorrect.");
            return;
        }

        if (newPwd.length() < 6) {
            AlertHelper.showWarning("Weak Password", "New password must be at least 6 characters.");
            return;
        }

        if (!newPwd.equals(reEnter)) {
            AlertHelper.showWarning("Mismatch", "New passwords do not match.");
            return;
        }

        btnChangePwd.setDisable(true);
        btnChangePwd.setText("Updating...");

        AsyncExecutor.getInstance().execute(() -> {
            try {
                String newHash = PasswordUtil.hashPassword(newPwd);
                currentUser.setPasswordHash(newHash);
                DAOFactory.getInstance().getUserDAO().update(currentUser);

                Platform.runLater(() -> {
                    btnChangePwd.setDisable(false);
                    btnChangePwd.setText("Change Password");
                    AlertHelper.showSuccess("Password Changed", "Your password has been successfully updated.");
                    clearFields();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    btnChangePwd.setDisable(false);
                    btnChangePwd.setText("Change Password");
                    AlertHelper.showError("Update Failed", e.getMessage());
                });
            }
        });
    }

    @FXML
    private void imgOldPwdClicked(MouseEvent event) {
        // Toggle view
    }

    private void clearFields() {
        if (txtOldPwd != null) txtOldPwd.clear();
        if (txtNewPwd != null) txtNewPwd.clear();
        if (txtReEnterPwd != null) txtReEnterPwd.clear();
    }

    private void loadAvatar() {
        if (ivAvatar == null) return;
        try (InputStream stream = getClass().getResourceAsStream("/com/example/car_rental_sys/image/avatar/1.png")) {
            if (stream != null) ivAvatar.setImage(new Image(stream));
        } catch (Exception ignored) {
        }
    }
}

