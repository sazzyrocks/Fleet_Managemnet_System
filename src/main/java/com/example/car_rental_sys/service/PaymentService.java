package com.example.car_rental_sys.service;

import com.example.car_rental_sys.dao.BankCardDAO;
import com.example.car_rental_sys.dao.DAOFactory;
import com.example.car_rental_sys.dao.OrderDAO;
import com.example.car_rental_sys.dao.PaymentDAO;
import com.example.car_rental_sys.db.TransactionManager;
import com.example.car_rental_sys.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service managing customer payment methods and transactional checkout settlements.
 */
public class PaymentService {

    private final PaymentDAO paymentDAO;
    private final BankCardDAO bankCardDAO;
    private final OrderDAO orderDAO;

    public PaymentService() {
        this(DAOFactory.getInstance().getPaymentDAO(),
             DAOFactory.getInstance().getBankCardDAO(),
             DAOFactory.getInstance().getOrderDAO());
    }

    public PaymentService(PaymentDAO paymentDAO, BankCardDAO bankCardDAO, OrderDAO orderDAO) {
        this.paymentDAO = paymentDAO;
        this.bankCardDAO = bankCardDAO;
        this.orderDAO = orderDAO;
    }

    public Payment processPayment(Payment payment) {
        if (payment == null) {
            throw new ServiceException("Payment details cannot be null.");
        }
        if (payment.getOrderId() == null) {
            throw new ServiceException("Payment order ID must not be null.");
        }
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        if (payment.getTransactionReference() == null) {
            payment.setTransactionReference("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        return paymentDAO.save(payment);
    }

    public BigDecimal getTotalRevenue() {
        return paymentDAO.findAll().stream()
                .filter(p -> PaymentStatus.SUCCESS.equals(p.getPaymentStatus()))
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Processes payment for a rental order with ACID transactional atomicity.
     *
     * @param orderId order ID being paid
     * @param cardId  card ID used for debiting
     * @return recorded Payment
     */
    public Payment processPayment(int orderId, int cardId) {
        Order order = orderDAO.findById(orderId)
                .orElseThrow(() -> new ServiceException("Order not found: ID " + orderId));

        if (OrderStatus.CONFIRMED.equals(order.getStatus()) || OrderStatus.COMPLETED.equals(order.getStatus())) {
            throw new ServiceException("Order is already paid and confirmed.");
        }

        BankCard card = bankCardDAO.findById(cardId)
                .orElseThrow(() -> new ServiceException("Payment card not found: ID " + cardId));

        BigDecimal amount = order.getTotalPrice();
        if (card.getBalance().compareTo(amount) < 0) {
            throw new ServiceException("Insufficient card balance. Required: $" + amount + ", Available: $" + card.getBalance());
        }

        // Execute atomically in a transaction
        return TransactionManager.execute(conn -> {
            // 1. Debit card balance
            BigDecimal newBalance = card.getBalance().subtract(amount);
            bankCardDAO.updateBalance(cardId, newBalance);

            // 2. Record payment record
            String txnRef = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setAmount(amount);
            payment.setPaymentMethod(card.getCardType());
            payment.setTransactionReference(txnRef);
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            Payment savedPayment = paymentDAO.save(payment);

            // 3. Transition order status to CONFIRMED
            orderDAO.updateStatus(orderId, OrderStatus.CONFIRMED);

            return savedPayment;
        });
    }

    public List<BankCard> getCustomerCards(int userId) {
        return bankCardDAO.findByUserId(userId);
    }

    public BankCard addCard(BankCard card) {
        if (card.getCardNumber() == null || card.getCardNumber().length() < 12) {
            throw new ServiceException("Valid card number is required.");
        }
        return bankCardDAO.save(card);
    }

    public boolean setDefaultCard(int userId, int cardId) {
        return bankCardDAO.setDefaultCard(userId, cardId);
    }

    public Optional<Payment> getPaymentForOrder(int orderId) {
        return paymentDAO.findByOrderId(orderId);
    }
}
