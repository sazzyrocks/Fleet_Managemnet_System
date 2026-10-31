package com.example.car_rental_sys.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Bank card model for customer wallet & rental payments.
 */
public class BankCard implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer cardId;
    private Integer userId;
    private String cardNumber;
    private String cardHolder;
    private String expiryDate;
    private String cvv;
    private BigDecimal balance;
    private String cardType;
    private Boolean isDefault;
    private LocalDateTime createdAt;

    public BankCard() {
        this.balance = BigDecimal.ZERO;
        this.cardType = "Visa";
        this.isDefault = false;
    }

    public BankCard(Integer userId, String cardNumber, String cardHolder, String expiryDate, String cvv, BigDecimal balance) {
        this();
        this.userId = userId;
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
    }

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns a masked representation of the card number (e.g. **** **** **** 1234) for UI safety.
     */
    public String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + lastFour;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BankCard bankCard)) return false;
        return Objects.equals(cardId, bankCard.cardId) && Objects.equals(cardNumber, bankCard.cardNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardId, cardNumber);
    }

    @Override
    public String toString() {
        return "BankCard{" +
                "cardId=" + cardId +
                ", cardHolder='" + cardHolder + '\'' +
                ", maskedNumber='" + getMaskedCardNumber() + '\'' +
                ", balance=" + balance +
                ", isDefault=" + isDefault +
                '}';
    }
}
