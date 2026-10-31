package com.example.car_rental_sys.service;

/**
 * Enterprise Service Factory providing centralized singleton access to all business services.
 */
public final class ServiceFactory {

    private static volatile ServiceFactory instance;

    private final AuthService authService;
    private final VehicleService vehicleService;
    private final RentalService rentalService;
    private final PaymentService paymentService;

    private ServiceFactory() {
        this.authService = new AuthService();
        this.vehicleService = new VehicleService();
        this.rentalService = new RentalService();
        this.paymentService = new PaymentService();
    }

    public static ServiceFactory getInstance() {
        if (instance == null) {
            synchronized (ServiceFactory.class) {
                if (instance == null) {
                    instance = new ServiceFactory();
                }
            }
        }
        return instance;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public VehicleService getVehicleService() {
        return vehicleService;
    }

    public RentalService getRentalService() {
        return rentalService;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }
}
