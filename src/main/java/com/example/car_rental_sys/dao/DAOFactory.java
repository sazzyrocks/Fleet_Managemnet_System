package com.example.car_rental_sys.dao;

import com.example.car_rental_sys.dao.impl.*;

/**
 * Enterprise Factory Pattern for managing and providing DAO singleton instances.
 *
 * <p>Decouples higher architectural tiers (Services and Controllers) from concrete
 * raw JDBC DAO implementations.</p>
 */
public final class DAOFactory {

    private static volatile DAOFactory instance;

    private final UserDAO userDAO;
    private final VehicleDAO vehicleDAO;
    private final VehicleCategoryDAO vehicleCategoryDAO;
    private final OrderDAO orderDAO;
    private final PaymentDAO paymentDAO;
    private final BankCardDAO bankCardDAO;
    private final DriverDAO driverDAO;
    private final VehicleDiagnosticDAO vehicleDiagnosticDAO;
    private final ReviewDAO reviewDAO;
    private final SystemLogDAO systemLogDAO;
    private final MessageDAO messageDAO;
    private final TodoDAO todoDAO;

    private DAOFactory() {
        this.userDAO = new UserDAOImpl();
        this.vehicleDAO = new VehicleDAOImpl();
        this.vehicleCategoryDAO = new VehicleCategoryDAOImpl();
        this.orderDAO = new OrderDAOImpl();
        this.paymentDAO = new PaymentDAOImpl();
        this.bankCardDAO = new BankCardDAOImpl();
        this.driverDAO = new DriverDAOImpl();
        this.vehicleDiagnosticDAO = new VehicleDiagnosticDAOImpl();
        this.reviewDAO = new ReviewDAOImpl();
        this.systemLogDAO = new SystemLogDAOImpl();
        this.messageDAO = new MessageDAOImpl();
        this.todoDAO = new TodoDAOImpl();
    }

    /**
     * Retrieves the singleton instance of the DAOFactory using thread-safe double-checked locking.
     *
     * @return singleton DAOFactory instance
     */
    public static DAOFactory getInstance() {
        if (instance == null) {
            synchronized (DAOFactory.class) {
                if (instance == null) {
                    instance = new DAOFactory();
                }
            }
        }
        return instance;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public VehicleDAO getVehicleDAO() {
        return vehicleDAO;
    }

    public VehicleCategoryDAO getVehicleCategoryDAO() {
        return vehicleCategoryDAO;
    }

    public OrderDAO getOrderDAO() {
        return orderDAO;
    }

    public PaymentDAO getPaymentDAO() {
        return paymentDAO;
    }

    public BankCardDAO getBankCardDAO() {
        return bankCardDAO;
    }

    public DriverDAO getDriverDAO() {
        return driverDAO;
    }

    public VehicleDiagnosticDAO getVehicleDiagnosticDAO() {
        return vehicleDiagnosticDAO;
    }

    public ReviewDAO getReviewDAO() {
        return reviewDAO;
    }

    public SystemLogDAO getSystemLogDAO() {
        return systemLogDAO;
    }

    public MessageDAO getMessageDAO() {
        return messageDAO;
    }

    public TodoDAO getTodoDAO() {
        return todoDAO;
    }
}
