package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.concurrency.AsyncExecutor;
import com.example.car_rental_sys.model.Vehicle;
import com.example.car_rental_sys.service.ServiceFactory;
import com.example.car_rental_sys.service.VehicleService;
import com.example.car_rental_sys.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.InputStream;
import java.util.List;

/**
 * Controller for the public Vehicle Catalog & Search Results.
 */
public class CarsListController extends Controller {

    @FXML
    private ScrollPane scrollPane;

    private FlowPane cardContainer;

    @FXML
    public void initialize() {
        setupCatalogContainer();
        loadAvailableVehicles();
    }

    private void setupCatalogContainer() {
        cardContainer = new FlowPane();
        cardContainer.setHgap(25);
        cardContainer.setVgap(25);
        cardContainer.setPadding(new Insets(30, 40, 40, 40));
        cardContainer.setStyle("-fx-background-color: transparent;");

        if (scrollPane != null) {
            scrollPane.setContent(cardContainer);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background: #151515; -fx-background-color: #151515; -fx-border-color: transparent;");
        }
    }

    private void loadAvailableVehicles() {
        Label loadingLabel = new Label("Loading available fleet catalog...");
        loadingLabel.setStyle("-fx-text-fill: #999999; -fx-font-size: 16px;");
        cardContainer.getChildren().setAll(loadingLabel);

        AsyncExecutor.getInstance().execute(() -> {
            try {
                VehicleService vehicleService = ServiceFactory.getInstance().getVehicleService();
                List<Vehicle> vehicles = vehicleService.getAvailableVehicles();
                if (vehicles.isEmpty()) {
                    // Fallback to all vehicles if none marked specifically AVAILABLE yet
                    vehicles = vehicleService.getAllVehicles();
                }

                final List<Vehicle> finalList = vehicles;
                Platform.runLater(() -> renderVehicleCards(finalList));
            } catch (Exception e) {
                Platform.runLater(() -> {
                    Label errorLabel = new Label("Failed to load vehicle catalog: " + e.getMessage());
                    errorLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 16px;");
                    cardContainer.getChildren().setAll(errorLabel);
                });
            }
        });
    }

    private void renderVehicleCards(List<Vehicle> vehicles) {
        cardContainer.getChildren().clear();

        if (vehicles.isEmpty()) {
            Label emptyLabel = new Label("No vehicles are currently available for rental.");
            emptyLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 18px;");
            cardContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Vehicle v : vehicles) {
            VBox card = createVehicleCard(v);
            cardContainer.getChildren().add(card);
        }
    }

    private VBox createVehicleCard(Vehicle vehicle) {
        VBox card = new VBox(12);
        card.setPrefWidth(350);
        card.setPrefHeight(380);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: #202020;" +
                "-fx-background-radius: 16px;" +
                "-fx-border-color: #333333;" +
                "-fx-border-radius: 16px;" +
                "-fx-border-width: 1px;" +
                "-fx-cursor: hand;"
        );

        // Header: Brand & Model
        Label titleLabel = new Label(vehicle.getBrand() + " " + vehicle.getModel());
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label categoryLabel = new Label(vehicle.getYear() != null ? vehicle.getYear() + " Model" : "Premium Series");
        categoryLabel.setStyle("-fx-text-fill: #00d26a; -fx-font-size: 12px; -fx-font-weight: bold;");

        // Vehicle Image Preview
        ImageView imageView = new ImageView();
        imageView.setFitHeight(160);
        imageView.setFitWidth(310);
        imageView.setPreserveRatio(true);
        loadVehicleImage(imageView, vehicle.getImageUrl());

        // Spec highlights
        HBox specs = new HBox(15);
        specs.setAlignment(Pos.CENTER_LEFT);
        Label seats = new Label((vehicle.getSeats() != null ? vehicle.getSeats() : 4) + " Seats");
        seats.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 13px;");
        Label fuel = new Label(vehicle.getFuelType() != null ? vehicle.getFuelType().name() : "Petrol");
        fuel.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 13px;");
        Label plate = new Label(vehicle.getLicensePlate() != null ? vehicle.getLicensePlate() : "N/A");
        plate.setStyle("-fx-text-fill: #777777; -fx-font-size: 12px;");
        specs.getChildren().addAll(seats, fuel, plate);

        // Footer: Price & Details button
        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_LEFT);
        VBox.setVgrow(footer, Priority.ALWAYS);

        Label priceLabel = new Label("$" + (vehicle.getDailyRate() != null ? vehicle.getDailyRate().toPlainString() : "150.00") + " / day");
        priceLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 16px; -fx-font-weight: bold;");
        HBox.setHgrow(priceLabel, Priority.ALWAYS);

        Button viewBtn = new Button("View Details");
        viewBtn.setStyle(
                "-fx-background-color: #1d90f4;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8px;" +
                "-fx-padding: 8 16 8 16;"
        );
        viewBtn.setOnAction(e -> openVehicleDetails(vehicle));

        footer.getChildren().addAll(priceLabel, viewBtn);
        card.getChildren().addAll(titleLabel, categoryLabel, imageView, specs, footer);
        card.setOnMouseClicked(e -> openVehicleDetails(vehicle));

        return card;
    }

    private void openVehicleDetails(Vehicle vehicle) {
        CarDetailsPageController.setSelectedVehicle(vehicle);
        SceneManager.switchScene("carDetailsPage.fxml");
    }

    private void loadVehicleImage(ImageView imageView, String imagePath) {
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

        // Fallback default car image
        try (InputStream fallback = getClass().getResourceAsStream("/com/example/car_rental_sys/image/cars/Huracan_EVI.png")) {
            if (fallback != null) {
                imageView.setImage(new Image(fallback));
            }
        } catch (Exception ignored) {
        }
    }
}

