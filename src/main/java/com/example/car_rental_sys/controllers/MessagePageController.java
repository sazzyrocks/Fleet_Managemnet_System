package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.concurrency.AsyncExecutor;
import com.example.car_rental_sys.dao.DAOFactory;
import com.example.car_rental_sys.model.Message;
import com.example.car_rental_sys.model.User;
import com.example.car_rental_sys.service.ServiceFactory;
import com.example.car_rental_sys.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;

/**
 * Controller for Real-Time Fleet and Customer Support Messaging.
 */
public class MessagePageController {

    @FXML
    private Pane mainPane;

    @FXML
    private Pane navBarPane;

    private WebView webView;

    @FXML
    public void initialize() {
        initBrowser();
    }

    private void initBrowser() {
        if (mainPane == null) {
            return;
        }

        try {
            webView = new WebView();
            webView.setPrefSize(1280, 832);
            mainPane.getChildren().add(0, webView);

            if (navBarPane != null) {
                navBarPane.toFront();
            }

            WebEngine engine = webView.getEngine();
            URL contactUrl = getClass().getResource("/com/example/car_rental_sys/html/contactUs/index.html");
            if (contactUrl != null) {
                engine.load(contactUrl.toExternalForm());
            }
        } catch (Exception e) {
            System.err.println("[MessagePageController] WebView initialization: " + e.getMessage());
        }
    }

    /**
     * Dispatches user back to their respective portal based on role.
     */
    public void backToService() {
        Platform.runLater(() -> {
            User user = ServiceFactory.getInstance().getAuthService().getCurrentUser();
            if (user == null) {
                SceneManager.switchScene("loginPage.fxml");
                return;
            }
            switch (user.getRole()) {
                case ADMIN:
                    SceneManager.switchScene("adminServicePage.fxml");
                    break;
                case DRIVER:
                    SceneManager.switchScene("driverServicePage.fxml");
                    break;
                case CUSTOMER:
                default:
                    SceneManager.switchScene("customerServicePage.fxml");
                    break;
            }
        });
    }
}

