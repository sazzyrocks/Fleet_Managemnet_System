package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.InputStream;

/**
 * Controller for the compact message modal navigation bar.
 */
public class NavigationBaMsgrController {

    @FXML
    private Pane navBarPanel;

    @FXML
    private ImageView closeIconBtn;

    @FXML
    private Pane transparentBtn;

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
        setImageFromClasspath(closeIconBtn, "/com/example/car_rental_sys/image/UI/closeGray.png");
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

