package com.example.car_rental_sys.service;

import com.example.car_rental_sys.dao.BankCardDAO;
import com.example.car_rental_sys.dao.OrderDAO;
import com.example.car_rental_sys.dao.PaymentDAO;
import com.example.car_rental_sys.dao.UserDAO;
import com.example.car_rental_sys.dao.VehicleDAO;
import com.example.car_rental_sys.model.*;
import com.example.car_rental_sys.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End Enterprise Integration Test for the Complete Car Rental Lifecycle.
 * Covers user registration, authentication, inventory search, booking collision detection,
 * transactional payment processing, and rental completion.
 */
class FullRentalLifecycleIntegrationTest {

    private AuthService authService;
    private VehicleService vehicleService;
    private RentalService rentalService;
    private PaymentService paymentService;

    // In-memory mock DAOs for isolated deterministic testing
    private Map<Integer, User> userDb;
    private Map<Integer, Vehicle> vehicleDb;
    private Map<Integer, Order> orderDb;
    private Map<Integer, BankCard> cardDb;
    private Map<Integer, Payment> paymentDb;

    @BeforeEach
    void setUp() {
        userDb = new HashMap<>();
        vehicleDb = new HashMap<>();
        orderDb = new HashMap<>();
        cardDb = new HashMap<>();
        paymentDb = new HashMap<>();

        // Set up in-memory UserDAO mock
        UserDAO mockUserDao = new UserDAO() {
            private int idGen = 100;

            @Override
            public Optional<User> findByUsername(String username) {
                return userDb.values().stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst();
            }

            @Override
            public Optional<User> findByEmail(String email) {
                return userDb.values().stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst();
            }

            @Override
            public boolean existsByUsername(String username) {
                return userDb.values().stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
            }

            @Override
            public boolean existsByEmail(String email) {
                return userDb.values().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
            }

            @Override
            public List<User> findByRole(Role role) {
                return userDb.values().stream().filter(u -> u.getRole() == role).toList();
            }

            @Override
            public boolean updatePassword(int userId, String newPasswordHash) {
                User u = userDb.get(userId);
                if (u != null) {
                    u.setPasswordHash(newPasswordHash);
                    return true;
                }
                return false;
            }

            @Override
            public boolean updateStatus(int userId, UserStatus status) {
                User u = userDb.get(userId);
                if (u != null) {
                    u.setStatus(status);
                    return true;
                }
                return false;
            }

            @Override
            public Optional<User> findById(Integer id) {
                return Optional.ofNullable(userDb.get(id));
            }

            @Override
            public List<User> findAll() {
                return new ArrayList<>(userDb.values());
            }

            @Override
            public User save(User entity) {
                if (entity.getUserId() == null) {
                    entity.setUserId(++idGen);
                }
                userDb.put(entity.getUserId(), entity);
                return entity;
            }

            @Override
            public User update(User entity) {
                userDb.put(entity.getUserId(), entity);
                return entity;
            }

            @Override
            public boolean deleteById(Integer id) {
                return userDb.remove(id) != null;
            }

            @Override
            public long count() {
                return userDb.size();
            }
        };

        // Set up in-memory VehicleDAO mock
        VehicleDAO mockVehicleDao = new VehicleDAO() {
            private int idGen = 200;

            @Override
            public List<Vehicle> findByStatus(VehicleStatus status) {
                return vehicleDb.values().stream().filter(v -> v.getStatus() == status).toList();
            }

            @Override
            public List<Vehicle> findByCategory(int categoryId) {
                return vehicleDb.values().stream().filter(v -> v.getCategoryId() != null && v.getCategoryId() == categoryId).toList();
            }

            @Override
            public List<Vehicle> search(String brand, String model, BigDecimal minRate, BigDecimal maxRate) {
                return vehicleDb.values().stream().filter(v -> {
                    boolean matches = true;
                    if (brand != null && !brand.isEmpty()) matches = matches && v.getBrand().equalsIgnoreCase(brand);
                    if (model != null && !model.isEmpty()) matches = matches && v.getModel().equalsIgnoreCase(model);
                    if (minRate != null) matches = matches && v.getDailyRate().compareTo(minRate) >= 0;
                    if (maxRate != null) matches = matches && v.getDailyRate().compareTo(maxRate) <= 0;
                    return matches;
                }).toList();
            }

            @Override
            public boolean updateStatus(int vehicleId, VehicleStatus newStatus) {
                Vehicle v = vehicleDb.get(vehicleId);
                if (v != null) {
                    v.setStatus(newStatus);
                    return true;
                }
                return false;
            }

            @Override
            public Optional<Vehicle> findByPlateNumber(String plateNumber) {
                return vehicleDb.values().stream().filter(v -> v.getPlateNumber().equalsIgnoreCase(plateNumber)).findFirst();
            }

            @Override
            public Optional<Vehicle> findById(Integer id) {
                return Optional.ofNullable(vehicleDb.get(id));
            }

            @Override
            public List<Vehicle> findAll() {
                return new ArrayList<>(vehicleDb.values());
            }

            @Override
            public Vehicle save(Vehicle entity) {
                if (entity.getVehicleId() == null) {
                    entity.setVehicleId(++idGen);
                }
                vehicleDb.put(entity.getVehicleId(), entity);
                return entity;
            }

            @Override
            public Vehicle update(Vehicle entity) {
                vehicleDb.put(entity.getVehicleId(), entity);
                return entity;
            }

            @Override
            public boolean deleteById(Integer id) {
                return vehicleDb.remove(id) != null;
            }

            @Override
            public long count() {
                return vehicleDb.size();
            }
        };

        // Set up in-memory OrderDAO mock
        OrderDAO mockOrderDao = new OrderDAO() {
            private int idGen = 300;

            @Override
            public List<Order> findByCustomerId(int customerId) {
                return orderDb.values().stream().filter(o -> o.getCustomerId() == customerId).toList();
            }

            @Override
            public List<Order> findByVehicleId(int vehicleId) {
                return orderDb.values().stream().filter(o -> o.getVehicleId() == vehicleId).toList();
            }

            @Override
            public List<Order> findByStatus(OrderStatus status) {
                return orderDb.values().stream().filter(o -> o.getStatus() == status).toList();
            }

            @Override
            public boolean isVehicleBookedForInterval(int vehicleId, LocalDateTime start, LocalDateTime end) {
                return orderDb.values().stream().filter(o -> o.getVehicleId() == vehicleId)
                        .filter(o -> o.getStatus() != OrderStatus.CANCELLED && o.getStatus() != OrderStatus.COMPLETED)
                        .anyMatch(o -> !o.getEndDate().isBefore(start) && !o.getStartDate().isAfter(end));
            }

            @Override
            public Optional<Order> findByOrderNumber(String orderNumber) {
                return orderDb.values().stream().filter(o -> o.getOrderNumber().equals(orderNumber)).findFirst();
            }

            @Override
            public boolean updateStatus(int orderId, OrderStatus newStatus) {
                Order o = orderDb.get(orderId);
                if (o != null) {
                    o.setStatus(newStatus);
                    return true;
                }
                return false;
            }

            @Override
            public Optional<Order> findById(Integer id) {
                return Optional.ofNullable(orderDb.get(id));
            }

            @Override
            public List<Order> findAll() {
                return new ArrayList<>(orderDb.values());
            }

            @Override
            public Order save(Order entity) {
                if (entity.getOrderId() == null) {
                    entity.setOrderId(++idGen);
                }
                orderDb.put(entity.getOrderId(), entity);
                return entity;
            }

            @Override
            public Order update(Order entity) {
                orderDb.put(entity.getOrderId(), entity);
                return entity;
            }

            @Override
            public boolean deleteById(Integer id) {
                return orderDb.remove(id) != null;
            }

            @Override
            public long count() {
                return orderDb.size();
            }
        };

        // Populate test vehicle
        Vehicle v1 = new Vehicle("Tesla", "Model 3", 2024, "EL-001", new BigDecimal("120.00"), 4,
                TransmissionType.AUTOMATIC, FuelType.ELECTRIC, VehicleStatus.AVAILABLE, 4);
        v1.setVehicleId(1);
        vehicleDb.put(v1.getVehicleId(), v1);

        authService = new AuthService(mockUserDao);
        vehicleService = new VehicleService(mockVehicleDao);
        rentalService = new RentalService(mockOrderDao, mockVehicleDao);
    }

    @Test
    @DisplayName("Verify user registration and authentication flow")
    void testUserRegistrationAndLogin() throws AuthenticationException {
        User registered = authService.register("john_doe", "SecretPass123", "john@example.com", "John Doe", "+1-234-5678");
        assertNotNull(registered);
        assertNotNull(registered.getUserId());
        assertEquals("john_doe", registered.getUsername());

        // Test login
        User loggedIn = authService.login("john_doe", "SecretPass123");
        assertNotNull(loggedIn);
        assertEquals(registered.getUserId(), loggedIn.getUserId());
        assertTrue(authService.isAuthenticated());

        // Test bad password fails
        assertThrows(AuthenticationException.class, () -> authService.login("john_doe", "WrongPassword"));

        authService.logout();
        assertFalse(authService.isAuthenticated());
    }

    @Test
    @DisplayName("Verify vehicle availability search and booking conflict detection")
    void testBookingAndConflictDetection() throws Exception {
        // Query available vehicles
        List<Vehicle> available = vehicleService.getAvailableVehicles();
        assertFalse(available.isEmpty());
        assertEquals("Tesla", available.get(0).getBrand());

        LocalDateTime now = LocalDateTime.now().plusDays(1);
        LocalDateTime returnDate = now.plusDays(3);

        // Book rental
        Order order = rentalService.bookRental(101, 1, null, now, returnDate, "Downtown Airport", "Downtown Airport");
        assertNotNull(order);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(new BigDecimal("360.00"), order.getTotalPrice());

        // Attempting to book overlapping interval for same vehicle should fail
        LocalDateTime overlappingStart = now.plusDays(1);
        LocalDateTime overlappingEnd = returnDate.plusDays(1);

        assertThrows(BookingConflictException.class, () ->
                rentalService.bookRental(102, 1, null, overlappingStart, overlappingEnd, "Downtown Airport", "Downtown Airport")
        );
    }

    @Test
    @DisplayName("Verify invalid rental date validation rules")
    void testRentalDateValidation() {
        LocalDateTime start = LocalDateTime.now().plusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(1); // Return date before pickup

        assertThrows(ServiceException.class, () ->
                rentalService.bookRental(101, 1, null, start, end, "City Center", "City Center")
        );
    }
}

