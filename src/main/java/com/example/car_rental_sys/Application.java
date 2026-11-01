package com.example.car_rental_sys;

import com.example.car_rental_sys.concurrency.AsyncExecutor;
import com.example.car_rental_sys.util.SceneManager;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.logging.Logger;

/**
 * Main Application Entry Point for the JavaFX Enterprise Fleet Management System.
 */
public class Application extends javafx.application.Application {

    private static final Logger LOGGER = Logger.getLogger(Application.class.getName());

    @Override
    public void start(Stage stage) {
        LOGGER.info("Starting Fleet Management System desktop client...");

        // Standard desktop window with OS titlebar (minimize, maximize, close, drag)
        stage.setTitle("Fleet Management System");
        stage.setResizable(true);

        SceneManager.setPrimaryStage(stage);
        SceneManager.setAppIcon();

        // Launch with the splash loading screen
        SceneManager.switchScene("loadingPage.fxml");
    }

    @Override
    public void stop() {
        LOGGER.info("Shutting down Fleet Management System background workers...");
        AsyncExecutor.getInstance().shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

