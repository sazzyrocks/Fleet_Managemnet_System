package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.concurrency.AsyncExecutor;
import com.example.car_rental_sys.model.Order;
import com.example.car_rental_sys.model.Payment;
import com.example.car_rental_sys.model.PaymentStatus;
import com.example.car_rental_sys.model.User;
import com.example.car_rental_sys.model.Vehicle;
import com.example.car_rental_sys.service.PaymentService;
import com.example.car_rental_sys.service.RentalService;
import com.example.car_rental_sys.service.ServiceFactory;
import com.example.car_rental_sys.util.AlertHelper;
import com.example.car_rental_sys.util.SceneManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Controller for Rental Checkout and Payment Processing.
 */
public class PaymentController extends Controller {

    @FXML
    public Pane mainPane;

    @FXML
    private ImageView avatarImageView;

    @FXML
    private Label customerName;

    @FXML
    private Label price;

    @FXML
    private Label duration;

    @FXML
    private Label discount;

    @FXML
    private Label tax;

    @FXML
    private Label totalPrice;

    @FXML
    private Label pickUpTime;

    @FXML
    private Label returnTime;

    @FXML
    private Button carChooseBtn;

    @FXML
    private Label carChooseText;

    @FXML
    private Label modelText;

    @FXML
    private Label priceText;

    @FXML
    private ImageView carImageView;

    @FXML
    private Label balanceText;

    @FXML
    private RadioButton radioBtn1;

    @FXML
    private RadioButton radioBtn2;

    @FXML
    private Label cardTypeAndNum;

    @FXML
    private Label expiresDate;

    @FXML
    private ImageView bankcardImage;

    @FXML
    private Button downIconBtn;

    @FXML
    private ScrollPane bankCardsContainer;

    private Vehicle currentVehicle;
    private BigDecimal calculatedTotal;

    @FXML
    public void initialize() {
        currentVehicle = CarDetailsPageController.getSelectedVehicle();
        setupPaymentMethod();
        populateCustomerData();
        populatePricingSummary();
        if (bankCardsContainer != null) {
            bankCardsContainer.setVisible(false);
        }
    }

    private void setupPaymentMethod() {
        if (radioBtn2 != null) radioBtn2.setSelected(true);
        if (radioBtn1 != null) radioBtn1.setSelected(false);
    }

    private void populateCustomerData() {
        User user = ServiceFactory.getInstance().getAuthService().getCurrentUser();
        if (user != null) {
            if (customerName != null) {
                customerName.setText(user.getFullName() != null ? user.getFullName() : user.getUsername());
            }
            if (balanceText != null) {
                balanceText.setText("$5,000.00");
            }
        }
    }

    private void populatePricingSummary() {
        BigDecimal dailyRate = (currentVehicle != null && currentVehicle.getDailyRate() != null)
                ? currentVehicle.getDailyRate()
                : new BigDecimal("150.00");

        String brand = currentVehicle != null ? currentVehicle.getBrand() : "Lamborghini";
        String model = currentVehicle != null ? currentVehicle.getModel() : "Huracan";

        if (modelText != null) modelText.setText(brand + "_" + model);
        if (priceText != null) priceText.setText("$" + dailyRate.toPlainString() + "/D");
        if (price != null) price.setText("$" + dailyRate.divide(new BigDecimal("24"), 2, RoundingMode.HALF_UP) + "/H");
        if (duration != null) duration.setText("48 Hours (2 Days)");
        if (discount != null) discount.setText("-$20.00");
        if (tax != null) tax.setText("6%");

        BigDecimal base = dailyRate.multiply(new BigDecimal("2"));
        BigDecimal taxAmount = base.multiply(new BigDecimal("0.06"));
        calculatedTotal = base.add(taxAmount).subtract(new BigDecimal("20.00"));
        if (totalPrice != null) {
            totalPrice.setText("$" + calculatedTotal.setScale(2, RoundingMode.HALF_UP).toPlainString());
        }

        LocalDateTime now = LocalDateTime.now();
        if (pickUpTime != null) pickUpTime.setText(now.plusHours(2).toLocalDate().toString());
        if (returnTime != null) returnTime.setText(now.plusHours(50).toLocalDate().toString());

        loadCarImage(carImageView);
    }

    @FXML
    private void makePaymentBtnClickEvent(ActionEvent event) {
        User user = ServiceFactory.getInstance().getAuthService().getCurrentUser();
        if (user == null) {
            AlertHelper.showWarning("Session Expired", "Please log in to complete the transaction.");
            SceneManager.switchScene("loginPage.fxml");
            return;
        }

        if (currentVehicle == null) {
            AlertHelper.showWarning("No Vehicle Selected", "Please select a vehicle from the catalog.");
            SceneManager.switchScene("carsListPage.fxml");
            return;
        }

        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = LocalDateTime.now().plusHours(50);

        AsyncExecutor.getInstance().execute(() -> {
            try {
                RentalService rentalService = ServiceFactory.getInstance().getRentalService();
                PaymentService paymentService = ServiceFactory.getInstance().getPaymentService();

                // 1. Create reservation
                Order order = rentalService.createReservation(
                        user.getUserId(),
                        currentVehicle.getVehicleId(),
                        start,
                        end,
                        "Central Terminal",
                        "Central Terminal"
                );

                // 2. Process atomic payment
                Payment payment = new Payment();
                payment.setOrderId(order.getOrderId());
                payment.setAmount(calculatedTotal != null ? calculatedTotal : new BigDecimal("280.00"));
                payment.setPaymentMethod(radioBtn1 != null && radioBtn1.isSelected() ? "PayPal" : "CreditCard");
                payment.setStatus(PaymentStatus.COMPLETED);

                paymentService.processPayment(payment);

                // 3. Confirm reservation
                rentalService.confirmBooking(order.getOrderId());

                Platform.runLater(() -> {
                    OrderDetailsComponentController.setSelectedOrder(order);
                    SceneManager.switchScene("paySuccessPage.fxml");
                });
            } catch (Exception e) {
                Platform.runLater(() -> AlertHelper.showError("Payment Failed", e.getMessage()));
            }
        });
    }

    @FXML
    private void goToCarListPage(MouseEvent event) {
        SceneManager.switchScene("carsListPage.fxml");
    }

    @FXML
    private void viewCarDetailBtnClickEvent(MouseEvent event) {
        SceneManager.switchScene("carDetailsPage.fxml");
    }

    @FXML
    private void selectRadioBtn1(MouseEvent event) {
        if (radioBtn1 != null) radioBtn1.setSelected(true);
        if (radioBtn2 != null) radioBtn2.setSelected(false);
    }

    @FXML
    private void selectRadioBtn2(MouseEvent event) {
        if (radioBtn2 != null) radioBtn2.setSelected(true);
        if (radioBtn1 != null) radioBtn1.setSelected(false);
    }

    @FXML
    private void showBankCardChooser(MouseEvent event) {
        if (bankCardsContainer != null) {
            bankCardsContainer.setVisible(!bankCardsContainer.isVisible());
        }
    }

    private void loadCarImage(ImageView imageView) {
        if (imageView == null) return;
        try (InputStream stream = getClass().getResourceAsStream("/com/example/car_rental_sys/image/cars/Huracan_EVI.png")) {
            if (stream != null) imageView.setImage(new Image(stream));
        } catch (Exception ignored) {
        }
    }
}

