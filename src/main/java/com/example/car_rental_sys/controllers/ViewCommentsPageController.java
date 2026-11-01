package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.concurrency.AsyncExecutor;
import com.example.car_rental_sys.dao.DAOFactory;
import com.example.car_rental_sys.dao.ReviewDAO;
import com.example.car_rental_sys.model.Review;
import com.example.car_rental_sys.model.User;
import com.example.car_rental_sys.model.Vehicle;
import com.example.car_rental_sys.service.ServiceFactory;
import com.example.car_rental_sys.util.AlertHelper;
import com.example.car_rental_sys.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for Customer Vehicle Reviews and Feedback Portal.
 */
public class ViewCommentsPageController {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox vBoxContainer;

    @FXML
    private ImageView backBtn, sendIcon;

    @FXML
    private Label titleLabel;

    @FXML
    public TextArea textArea;

    public static ViewCommentsPageController instance;

    private Vehicle currentVehicle;
    private final ReviewDAO reviewDAO = DAOFactory.getInstance().getReviewDAO();
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    private void initialize() {
        instance = this;
        currentVehicle = CarDetailsPageController.getSelectedVehicle();

        initHeader();
        initScrollPane();
        initEvents();
        loadCommentsAsync();
    }

    private void initHeader() {
        if (titleLabel != null) {
            if (currentVehicle != null) {
                titleLabel.setText("Comments on " + currentVehicle.getBrand() + " " + currentVehicle.getModel());
            } else {
                titleLabel.setText("Vehicle Feedback & Community Reviews");
            }
        }
    }

    private void initScrollPane() {
        if (scrollPane != null) {
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        }
    }

    private void initEvents() {
        if (backBtn != null) {
            backBtn.setOnMouseClicked(event -> SceneManager.switchScene("carDetailsPage.fxml"));
        }

        if (sendIcon != null) {
            sendIcon.setOnMouseClicked(event -> postComment());
        }

        if (textArea != null) {
            textArea.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) {
                    event.consume();
                    postComment();
                }
            });
        }
    }

    private void loadCommentsAsync() {
        if (currentVehicle == null || currentVehicle.getVehicleId() == null) {
            addSampleComment("Demo User", "Very smooth driving experience and excellent fuel efficiency!", LocalDateTime.now().minusHours(3));
            return;
        }

        int vehicleId = currentVehicle.getVehicleId();
        AsyncExecutor.getInstance().executeAsync(
                () -> reviewDAO.findByVehicleId(vehicleId),
                reviews -> {
                    if (vBoxContainer != null) {
                        vBoxContainer.getChildren().clear();
                        if (reviews.isEmpty()) {
                            addSampleComment("First Reviewer", "No comments yet. Be the first to share your experience with this vehicle!", LocalDateTime.now());
                        } else {
                            for (Review review : reviews) {
                                String author = review.getUserName() != null ? review.getUserName() : ("Customer #" + review.getUserId());
                                addCommentCard(author, review.getCommentText(), review.getCreatedAt(), review.getRating());
                            }
                        }
                    }
                },
                error -> {
                    System.err.println("[ViewCommentsPageController] Error loading reviews: " + error.getMessage());
                }
        );
    }

    private void postComment() {
        if (textArea == null) {
            return;
        }
        String text = textArea.getText() != null ? textArea.getText().trim() : "";
        if (text.isEmpty()) {
            return;
        }

        User user = ServiceFactory.getInstance().getAuthService().getCurrentUser();
        if (user == null) {
            AlertHelper.showWarning("Sign In Required", "Please log in to submit a review.");
            return;
        }

        Integer vehicleId = currentVehicle != null ? currentVehicle.getVehicleId() : 1;
        Review review = new Review();
        review.setUserId(user.getUserId());
        review.setUserName(user.getFullName() != null ? user.getFullName() : user.getUsername());
        review.setVehicleId(vehicleId);
        review.setRating(5);
        review.setCommentText(text);
        review.setCreatedAt(LocalDateTime.now());

        textArea.clear();

        AsyncExecutor.getInstance().executeAsync(
                () -> reviewDAO.save(review),
                saved -> {
                    addCommentCard(review.getUserName(), review.getCommentText(), review.getCreatedAt(), review.getRating());
                    if (scrollPane != null) {
                        scrollPane.setVvalue(1.0);
                    }
                },
                error -> {
                    AlertHelper.showError("Review Error", "Failed to post review: " + error.getMessage());
                }
        );
    }

    private void addSampleComment(String author, String text, LocalDateTime time) {
        addCommentCard(author, text, time, 5);
    }

    private void addCommentCard(String author, String text, LocalDateTime time, Integer rating) {
        if (vBoxContainer == null) {
            return;
        }

        VBox card = new VBox(6);
        card.setPadding(new Insets(12, 16, 12, 16));
        card.setStyle("-fx-background-color: #1e1e1e; -fx-background-radius: 8px; -fx-border-color: #333333; -fx-border-radius: 8px;");

        Label authorLabel = new Label(author != null ? author : "Anonymous");
        authorLabel.setTextFill(Color.web("#cae943"));
        authorLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        int stars = rating != null ? rating : 5;
        StringBuilder starsText = new StringBuilder();
        for (int i = 0; i < stars; i++) starsText.append("★ ");
        Label starsLabel = new Label(starsText.toString().trim());
        starsLabel.setTextFill(Color.web("#f59e0b"));
        starsLabel.setFont(Font.font("System", 12));

        Label textLabel = new Label(text);
        textLabel.setTextFill(Color.WHITE);
        textLabel.setWrapText(true);
        textLabel.setFont(Font.font("System", 13));

        String timeStr = time != null ? time.format(TIME_FMT) : LocalDateTime.now().format(TIME_FMT);
        Label timeLabel = new Label(timeStr);
        timeLabel.setTextFill(Color.web("#888888"));
        timeLabel.setFont(Font.font("System", 11));

        card.getChildren().addAll(authorLabel, starsLabel, textLabel, timeLabel);
        vBoxContainer.getChildren().add(card);
    }
}

