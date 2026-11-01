package com.example.car_rental_sys.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Controller for visual milestone dots in the Order Tracking component.
 */
public class TrackingStatusController extends Controller {

    @FXML
    private Pane panelStatus;

    @FXML
    private Pane pane1, pane2, pane3, pane4;

    @FXML
    private ImageView imgView1, imgView2, imgView3, imgView4;

    @FXML
    private Pane dotStatus1, dotStatus2, dotStatus3, dotStatus4;

    @FXML
    private Label lblStatusText1, lblStatusText2, lblStatusText3, lblStatusText4;

    @FXML
    public void initialize() {
        setMilestone(2); // By default, order placed and ready to collect
    }

    public void setMilestone(int stage) {
        Pane[] dots = {dotStatus1, dotStatus2, dotStatus3, dotStatus4};
        Label[] labels = {lblStatusText1, lblStatusText2, lblStatusText3, lblStatusText4};

        for (int i = 0; i < dots.length; i++) {
            if (dots[i] != null) {
                if (i <= stage) {
                    dots[i].setStyle("-fx-background-color: #165dff; -fx-background-radius: 10px;");
                } else {
                    dots[i].setStyle("-fx-background-color: #c9cdd4; -fx-background-radius: 10px;");
                }
            }
            if (labels[i] != null) {
                if (i <= stage) {
                    labels[i].setStyle("-fx-text-fill: #1d2129; -fx-font-weight: bold;");
                } else {
                    labels[i].setStyle("-fx-text-fill: #86909c; -fx-font-weight: normal;");
                }
            }
        }
    }
}

