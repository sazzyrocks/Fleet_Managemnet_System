package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.model.Role;
import com.example.car_rental_sys.model.User;
import com.example.car_rental_sys.service.AuthService;
import com.example.car_rental_sys.service.ServiceFactory;
import com.example.car_rental_sys.util.SceneManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.InputStream;

/**
 * Controller for the top navigation bar component.
 */
public class NavigationBarController {

    @FXML
    private Pane navBarPanel;

    @FXML
    private ImageView logoImage;

    @FXML
    private ImageView closeIconBtn;

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void initialize() {
        setupWindowDrag();
    }

    private void setupWindowDrag() {
        if (navBarPanel != null) {
            navBarPanel.setOnMousePressed(event -> {
                Stage stage = SceneManager.getPrimaryStage();
                if (stage != null) {
                    xOffset = stage.getX() - event.getScreenX();
                    yOffset = stage.getY() - event.getScreenY();
                }
            });

            navBarPanel.setOnMouseDragged(event -> {
                Stage stage = SceneManager.getPrimaryStage();
                if (stage != null) {
                    stage.setX(event.getScreenX() + xOffset);
                    stage.setY(event.getScreenY() + yOffset);
                }
            });
        }
    }

    @FXML
    private void homeBtnClick(ActionEvent event) {
        SceneManager.switchScene("mainPage.fxml");
    }

    @FXML
    private void serviceBtnClick(ActionEvent event) {
        AuthService authService = ServiceFactory.getInstance().getAuthService();
        User currentUser = authService.getCurrentUser();

        if (currentUser == null) {
            SceneManager.switchScene("loginPage.fxml");
        } else if (currentUser.getRole() == Role.ADMIN) {
            SceneManager.switchScene("adminServicePage.fxml");
        } else if (currentUser.getRole() == Role.DRIVER) {
            SceneManager.switchScene("driverServicePage.fxml");
        } else {
            SceneManager.switchScene("customerServicePage.fxml");
        }
    }

    @FXML
    private void aboutBtnClick(ActionEvent event) {
        SceneManager.switchScene("aboutUsPage.fxml");
    }

    @FXML
    private void contactBtnClick(ActionEvent event) {
        SceneManager.switchScene("contactUsPage.fxml");
    }

    @FXML
    private void closeIconBtnClicked(MouseEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void changeImageToHover(MouseEvent event) {
        setImageFromClasspath(closeIconBtn, "/com/example/car_rental_sys/image/UI/closeIconHover.png");
    }

    @FXML
    private void changeImageToClose(MouseEvent event) {
        setImageFromClasspath(closeIconBtn, "/com/example/car_rental_sys/image/UI/closeIcon.png");
    }

    private void setImageFromClasspath(ImageView imageView, String resourcePath) {
        if (imageView != null) {
            try (InputStream stream = getClass().getResourceAsStream(resourcePath)) {
                if (stream != null) {
                    imageView.setImage(new Image(stream));
                }
            } catch (Exception ignored) {
            }
        }
    }
}

