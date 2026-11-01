package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.model.Vehicle;
import com.example.car_rental_sys.service.AuthService;
import com.example.car_rental_sys.service.ServiceFactory;
import com.example.car_rental_sys.util.AlertHelper;
import com.example.car_rental_sys.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;

import java.io.InputStream;

/**
 * Controller for Vehicle Details & Rental Initiation.
 */
public class CarDetailsPageController extends Controller {

    private static Vehicle selectedVehicle;

    @FXML
    public Pane mainPane;

    @FXML
    private Pane containPane;

    @FXML
    private ImageView logImageView;

    @FXML
    private Label modelText;

    @FXML
    private Label starText;

    @FXML
    private ImageView carImageView;

    @FXML
    private Label speedText;

    @FXML
    private Label seatsText;

    @FXML
    private Label powerText;

    @FXML
    private WebView webview;

    @FXML
    private Label priceText;

    @FXML
    private ImageView backBtnImageView;

    @FXML
    private ImageView keyTipsImageView;

    @FXML
    private ScrollPane commentScrollPane;

    @FXML
    private Label viewMore;

    @FXML
    private Label commentsNum;

    public static void setSelectedVehicle(Vehicle vehicle) {
        selectedVehicle = vehicle;
    }

    public static Vehicle getSelectedVehicle() {
        return selectedVehicle;
    }

    @FXML
    public void initialize() {
        populateVehicleDetails();
        attachEventHandlers();
    }

    private void populateVehicleDetails() {
        if (selectedVehicle != null) {
            if (modelText != null) {
                modelText.setText(selectedVehicle.getBrand() + " " + selectedVehicle.getModel());
            }
            if (priceText != null && selectedVehicle.getDailyRate() != null) {
                priceText.setText("$" + selectedVehicle.getDailyRate().toPlainString() + "/Day");
            }
            if (seatsText != null) {
                seatsText.setText(String.valueOf(selectedVehicle.getSeats() != null ? selectedVehicle.getSeats() : 4));
            }
            if (speedText != null) {
                speedText.setText(selectedVehicle.getTransmission() != null ? selectedVehicle.getTransmission().name() : "310 km/h");
            }
            if (powerText != null) {
                powerText.setText(selectedVehicle.getFuelType() != null ? selectedVehicle.getFuelType().name() : "3.5 L");
            }
            if (starText != null) {
                starText.setText("4.9");
            }
            if (commentsNum != null) {
                commentsNum.setText("12 Reviews");
            }

            loadVehicleImage(carImageView, selectedVehicle.getImagePath());
        }
    }

    private void attachEventHandlers() {
        if (backBtnImageView != null) {
            backBtnImageView.setOnMouseClicked(event -> SceneManager.switchScene("carsListPage.fxml"));
        }
        if (viewMore != null) {
            viewMore.setOnMouseClicked(event -> SceneManager.switchScene("ViewCommentsPage.fxml"));
        }
    }

    @FXML
    private void rentNowBtnClickEvent(MouseEvent event) {
        AuthService authService = ServiceFactory.getInstance().getAuthService();
        if (!authService.isAuthenticated()) {
            AlertHelper.showInfo("Authentication Required", "Please log in or create an account to reserve this vehicle.");
            SceneManager.switchScene("loginPage.fxml");
            return;
        }

        // Proceed to payment / checkout
        SceneManager.switchScene("paymentPage.fxml");
    }

    private void loadVehicleImage(ImageView imageView, String imagePath) {
        if (imageView == null) return;
        String resourcePath = (imagePath != null && !imagePath.isBlank())
                ? imagePath
                : "/com/example/car_rental_sys/image/cars/Huracan_EVI.png";

        if (!resourcePath.startsWith("/")) {
            resourcePath = "/com/example/car_rental_sys/" + resourcePath;
        }

        try (InputStream stream = getClass().getResourceAsStream(resourcePath)) {
            if (stream != null) {
                imageView.setImage(new Image(stream));
                return;
            }
        } catch (Exception ignored) {
        }

        try (InputStream fallback = getClass().getResourceAsStream("/com/example/car_rental_sys/image/cars/Huracan_EVI.png")) {
            if (fallback != null) {
                imageView.setImage(new Image(fallback));
            }
        } catch (Exception ignored) {
        }
    }
}

