package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.concurrency.AsyncExecutor;
import com.example.car_rental_sys.dao.BankCardDAO;
import com.example.car_rental_sys.dao.DAOFactory;
import com.example.car_rental_sys.model.BankCard;
import com.example.car_rental_sys.model.User;
import com.example.car_rental_sys.service.ServiceFactory;
import com.example.car_rental_sys.util.AlertHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.List;

/**
 * Controller for displaying and managing a stored Credit/Debit Bank Card.
 */
public class showCardDetailsController extends Controller {

    @FXML
    private Label cardName;

    @FXML
    private Label cardValidDate;

    @FXML
    private Label cardNumber;

    @FXML
    private Label cardBankName;

    @FXML
    private Label cardHolderName;

    @FXML
    private Label cardBillingAddress;

    @FXML
    private Label lblTheme;

    @FXML
    private Pane themeWhite, themeRed, themeBlue, themeGreen, themeOrange;

    @FXML
    private TextArea txtBillingAddress;

    @FXML
    private TextField txtCardName;

    @FXML
    private Button cardEditBtn;

    @FXML
    private Button cardSaveBtn;

    @FXML
    private Button cardDeleteBtn;

    private BankCard activeCard;

    @FXML
    public void initialize() {
        loadCardDetails();
    }

    private void loadCardDetails() {
        User user = ServiceFactory.getInstance().getAuthService().getCurrentUser();
        if (user == null) return;

        AsyncExecutor.getInstance().execute(() -> {
            try {
                BankCardDAO cardDAO = DAOFactory.getInstance().getBankCardDAO();
                List<BankCard> cards = cardDAO.findByUserId(user.getUserId());
                if (!cards.isEmpty()) {
                    activeCard = cards.get(0);
                    Platform.runLater(this::renderActiveCard);
                }
            } catch (Exception ignored) {
            }
        });
    }

    private void renderActiveCard() {
        if (activeCard == null) return;

        if (cardName != null) cardName.setText(activeCard.getCardType() != null ? activeCard.getCardType() : "Primary Card");
        if (cardBankName != null) cardBankName.setText(activeCard.getBankName() != null ? activeCard.getBankName() : "Fleet Partner Bank");
        if (cardHolderName != null) cardHolderName.setText(activeCard.getCardHolder() != null ? activeCard.getCardHolder() : "Cardholder");

        if (cardNumber != null && activeCard.getCardNumber() != null) {
            String num = activeCard.getCardNumber();
            String masked = num.length() > 4 ? "**** **** **** " + num.substring(num.length() - 4) : "**** " + num;
            cardNumber.setText(masked);
        }

        if (cardValidDate != null) {
            cardValidDate.setText(String.format("%02d/%d", activeCard.getExpiryMonth() != null ? activeCard.getExpiryMonth() : 12,
                    activeCard.getExpiryYear() != null ? activeCard.getExpiryYear() % 100 : 28));
        }
    }

    @FXML
    private void cardEditClicked(MouseEvent event) {
        toggleEditUI(true);
    }

    @FXML
    private void cardSaveClicked(MouseEvent event) {
        if (activeCard != null && txtCardName != null && !txtCardName.getText().isBlank()) {
            activeCard.setCardType(txtCardName.getText().trim());
            AsyncExecutor.getInstance().execute(() -> {
                try {
                    DAOFactory.getInstance().getBankCardDAO().update(activeCard);
                    Platform.runLater(() -> {
                        renderActiveCard();
                        toggleEditUI(false);
                        AlertHelper.showSuccess("Card Updated", "Card details updated.");
                    });
                } catch (Exception ignored) {
                }
            });
        } else {
            toggleEditUI(false);
        }
    }

    @FXML
    private void cardDeleteClicked(MouseEvent event) {
        if (activeCard == null) return;
        boolean confirm = AlertHelper.showConfirmation("Remove Card", "Are you sure you want to remove this card from your wallet?");
        if (!confirm) return;

        AsyncExecutor.getInstance().execute(() -> {
            try {
                DAOFactory.getInstance().getBankCardDAO().deleteById(activeCard.getCardId());
                Platform.runLater(() -> {
                    activeCard = null;
                    if (cardNumber != null) cardNumber.setText("No card linked");
                    toggleEditUI(false);
                    AlertHelper.showSuccess("Card Removed", "Card removed from wallet.");
                });
            } catch (Exception ignored) {
            }
        });
    }

    private void toggleEditUI(boolean editing) {
        if (txtCardName != null) {
            txtCardName.setVisible(editing);
            if (editing && cardName != null) txtCardName.setText(cardName.getText());
        }
        if (txtBillingAddress != null) txtBillingAddress.setVisible(editing);
        if (cardName != null) cardName.setVisible(!editing);
        if (cardBillingAddress != null) cardBillingAddress.setVisible(!editing);

        if (cardSaveBtn != null) cardSaveBtn.setVisible(editing);
        if (cardDeleteBtn != null) cardDeleteBtn.setVisible(editing);
        if (cardEditBtn != null) cardEditBtn.setVisible(!editing);
    }

    @FXML
    private void themeWhiteClicked(MouseEvent event) {}

    @FXML
    private void themeRedClicked(MouseEvent event) {}

    @FXML
    private void themeBlueClicked(MouseEvent event) {}

    @FXML
    private void themeGreenClicked(MouseEvent event) {}

    @FXML
    private void themeOrangeClicked(MouseEvent event) {}
}

