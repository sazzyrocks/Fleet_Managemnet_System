package com.example.car_rental_sys.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Payment transaction record for rental orders.
 */
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer paymentId;
    private Integer orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private String transactionReference;
    private PaymentStatus paymentStatus;
    private LocalDateTime paidAt;

    public Payment() {
        this.paymentStatus = PaymentStatus.PENDING;
        this.paymentMethod = "CARD";
        this.amount = BigDecimal.ZERO;
        this.paidAt = LocalDateTime.now();
    }

    public Payment(Integer orderId, BigDecimal amount, String paymentMethod, String transactionReference, PaymentStatus paymentStatus) {
        this();
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionReference = transactionReference;
        this.paymentStatus = paymentStatus != null ? paymentStatus : PaymentStatus.PENDING;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public boolean isSuccessful() {
        return PaymentStatus.SUCCESS.equals(this.paymentStatus);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment payment)) return false;
        return Objects.equals(paymentId, payment.paymentId) && Objects.equals(transactionReference, payment.transactionReference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentId, transactionReference);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", orderId=" + orderId +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentStatus=" + paymentStatus +
                '}';
    }
}
