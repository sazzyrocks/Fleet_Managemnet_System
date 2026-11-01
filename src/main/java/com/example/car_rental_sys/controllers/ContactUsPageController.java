package com.example.car_rental_sys.controllers;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;

/**
 * Controller for the Contact Us inquiry and customer communication page.
 */
public class ContactUsPageController {

    @FXML
    private WebView webview;

    @FXML
    private void initialize() {
        initWebView();
    }

    private void initWebView() {
        if (webview == null) {
            return;
        }
        try {
            WebEngine engine = webview.getEngine();
            URL url = getClass().getResource("/com/example/car_rental_sys/html/contactUs/index1.html");
            if (url != null) {
                engine.load(url.toExternalForm());
            }
        } catch (Exception e) {
            System.err.println("[ContactUsPageController] Could not load contact html: " + e.getMessage());
        }
    }
}

