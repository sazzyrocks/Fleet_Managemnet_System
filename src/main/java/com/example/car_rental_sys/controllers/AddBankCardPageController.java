package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.concurrency.AsyncExecutor;
import com.example.car_rental_sys.dao.BankCardDAO;
import com.example.car_rental_sys.dao.DAOFactory;
import com.example.car_rental_sys.model.BankCard;
import com.example.car_rental_sys.model.User;
import com.example.car_rental_sys.service.ServiceFactory;
import com.example.car_rental_sys.util.AlertHelper;
import com.example.car_rental_sys.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * Controller for adding new payment methods and bank cards to the user wallet.
 */
public class AddBankCardPageController extends Controller {

    @FXML
    public Pane mainPane;

    @FXML
    private ImageView cardImage1, cardImage2, cardImage3, cardImageMain, backBtn;

    @FXML
    private ScrollPane testScrollPane;

    @FXML
    private RadioButton mastercardRadio;

    @FXML
    private RadioButton visaRadio;

    @FXML
    private RadioButton tngRadio;

    @FXML
    private RadioButton paypalRadio;

    @FXML
    private Pane typeMastercard, visaType, tngType, paypalType;

    @FXML
    private TextField txtCardName;

    @FXML
    private TextField txtCardHolder;

    @FXML
    private TextField txtCardNumber;

    @FXML
    private TextField txtValidMonth;

    @FXML
    private TextField txtValidDate;

    @FXML
    private ComboBox<String> banksComboBox;

    @FXML
    private TextArea billTextArea;

    @FXML
    private CheckBox sendMeCheckBox;

    @FXML
    private Button addCardButton;

    @FXML
    public void initialize() {
        populateBanks();
        selectRadio(mastercardRadio);
        attachEvents();
    }

    private void populateBanks() {
        if (banksComboBox != null) {
            banksComboBox.getItems().setAll(
                    "Maybank Berhad",
                    "CIMB Bank",
                    "Public Bank",
                    "RHB Bank",
                    "Citibank N.A.",
                    "JPMorgan Chase"
            );
            banksComboBox.getSelectionModel().selectFirst();
        }
    }

    private void attachEvents() {
        if (backBtn != null) {
            backBtn.setOnMouseClicked(event -> SceneManager.switchScene("BillingComponent.fxml"));
        }
        if (addCardButton != null) {
            addCardButton.setOnAction(e -> handleAddCard());
        }
    }

    @FXML
    private void cardRadioClick(MouseEvent event) {
        Object source = event.getSource();
        if (source == mastercardRadio) selectRadio(mastercardRadio);
        else if (source == visaRadio) selectRadio(visaRadio);
        else if (source == tngRadio) selectRadio(tngRadio);
        else if (source == paypalRadio) selectRadio(paypalRadio);
    }

    private void selectRadio(RadioButton selected) {
        RadioButton[] radios = {mastercardRadio, visaRadio, tngRadio, paypalRadio};
        for (RadioButton r : radios) {
            if (r != null) r.setSelected(r == selected);
        }
    }

    private void handleAddCard() {
        User user = ServiceFactory.getInstance().getAuthService().getCurrentUser();
        if (user == null) {
            AlertHelper.showWarning("Login Required", "Please log in to manage your cards.");
            SceneManager.switchScene("loginPage.fxml");
            return;
        }

        String cardNum = txtCardNumber != null ? txtCardNumber.getText().trim() : "";
        String holder = txtCardHolder != null ? txtCardHolder.getText().trim() : "";
        String bank = banksComboBox != null ? banksComboBox.getValue() : "Default Bank";

        if (cardNum.isEmpty() || holder.isEmpty()) {
            AlertHelper.showWarning("Incomplete Form", "Please enter the card number and cardholder name.");
            return;
        }

        BankCard card = new BankCard();
        card.setUserId(user.getUserId());
        card.setCardHolder(holder);
        card.setCardNumber(cardNum);
        card.setCardType(visaRadio != null && visaRadio.isSelected() ? "Visa" : "Mastercard");
        String month = txtValidMonth != null && !txtValidMonth.getText().isBlank() ? txtValidMonth.getText().trim() : "12";
        String year = txtValidDate != null && !txtValidDate.getText().isBlank() ? txtValidDate.getText().trim() : "28";
        card.setExpiryDate(month + "/" + year);
        card.setCvv("123");
        card.setBalance(new java.math.BigDecimal("1000.00"));

        addCardButton.setDisable(true);
        addCardButton.setText("Adding...");

        AsyncExecutor.getInstance().execute(() -> {
            try {
                BankCardDAO cardDAO = DAOFactory.getInstance().getBankCardDAO();
                cardDAO.save(card);

                Platform.runLater(() -> {
                    addCardButton.setDisable(false);
                    addCardButton.setText("Add Card");
                    AlertHelper.showSuccess("Card Linked", "Card " + card.getCardType() + " added to your digital wallet.");
                    SceneManager.switchScene("BillingComponent.fxml");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    addCardButton.setDisable(false);
                    addCardButton.setText("Add Card");
                    AlertHelper.showError("Failed to Add Card", e.getMessage());
                });
            }
        });
    }
}

