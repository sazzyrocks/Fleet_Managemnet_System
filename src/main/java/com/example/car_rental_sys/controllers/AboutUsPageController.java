package com.example.car_rental_sys.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;

import java.net.URL;

/**
 * Controller for About Us company narrative and vision presentation views.
 */
public class AboutUsPageController {

    @FXML
    private Label per, use, eng, des, bra;

    @FXML
    private Pane paneContainer, wordsContainer, mainPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private WebView webView;

    @FXML
    private void initialize() {
        initWebView();
        preventBrowserScrollingEvents();
        setOriginPosition();
        startFloatingTextAnimations();
    }

    private void initWebView() {
        if (webView == null) {
            return;
        }
        try {
            WebEngine engine = webView.getEngine();
            URL url = getClass().getResource("/com/example/car_rental_sys/html/aboutUs/index.html");
            if (url != null) {
                engine.load(url.toExternalForm());
            }
        } catch (Exception e) {
            System.err.println("[AboutUsPageController] Could not load embedded webview: " + e.getMessage());
        }
    }

    private void preventBrowserScrollingEvents() {
        if (mainPane == null || scrollPane == null) {
            return;
        }
        mainPane.addEventFilter(ScrollEvent.ANY, event -> {
            event.consume();
            scrollPane.setVvalue(scrollPane.getVvalue() - event.getDeltaY() / 1000.0);
        });
    }

    private void setOriginPosition() {
        if (per != null) per.setLayoutX(1280);
        if (use != null) use.setLayoutX(1280);
        if (eng != null) eng.setLayoutX(1280);
        if (des != null) des.setLayoutX(1280);
        if (bra != null) bra.setLayoutX(1280);
    }

    private void startFloatingTextAnimations() {
        if (per != null) {
            scheduleAnimation(per, 0.1);
        }
        if (des != null) {
            scheduleAnimation(des, 2.0);
        }
        if (use != null) {
            scheduleAnimation(use, 4.0);
        }
        if (bra != null) {
            scheduleAnimation(bra, 6.0);
        }
        if (eng != null) {
            scheduleAnimation(eng, 8.0);
        }
    }

    private void scheduleAnimation(Label label, double delaySeconds) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(delaySeconds), event -> {
            TranslateTransition translate = new TranslateTransition();
            translate.setByX(-1600);
            translate.setDuration(Duration.seconds(12));
            translate.setCycleCount(Timeline.INDEFINITE);
            translate.setNode(label);
            translate.play();
        }));
        timeline.play();
    }
}

