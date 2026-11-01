package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.util.SceneManager;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;

import java.net.URL;

/**
 * Controller for Payment Success Confirmation Screen.
 */
public class PaySuccessPageController extends Controller {

    @FXML
    public Pane mainPane;

    @FXML
    private WebView webview;

    @FXML
    private Pane navPane;

    @FXML
    public void initialize() {
        loadSuccessAnimation();
        createFallbackUi();
    }

    private void loadSuccessAnimation() {
        if (webview != null) {
            try {
                WebEngine engine = webview.getEngine();
                URL resource = getClass().getResource("/com/example/car_rental_sys/html/loading/loading.html");
                if (resource != null) {
                    engine.load(resource.toExternalForm());
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void createFallbackUi() {
        if (mainPane == null) return;

        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setLayoutX(390);
        box.setLayoutY(280);
        box.setPrefSize(500, 300);
        box.setStyle("-fx-background-color: #1a1a24; -fx-background-radius: 16px; -fx-padding: 30px;");

        Label title = new Label("Payment Successful!");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #00d26a;");

        Label desc = new Label("Your vehicle reservation has been confirmed. You can view your active booking in Order History.");
        desc.setStyle("-fx-font-size: 14px; -fx-text-fill: #bbbbbb;");
        desc.setWrapText(true);

        Button viewOrdersBtn = new Button("View My Orders");
        viewOrdersBtn.setStyle("-fx-background-color: #165dff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 24 10 24; -fx-background-radius: 8px; -fx-cursor: hand;");
        viewOrdersBtn.setOnAction(e -> SceneManager.switchScene("showOrderComponent.fxml"));

        Button returnHomeBtn = new Button("Return to Fleet Catalog");
        returnHomeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #86909c; -fx-cursor: hand;");
        returnHomeBtn.setOnAction(e -> SceneManager.switchScene("carsListPage.fxml"));

        box.getChildren().addAll(title, desc, viewOrdersBtn, returnHomeBtn);
        mainPane.getChildren().add(box);
    }
}

