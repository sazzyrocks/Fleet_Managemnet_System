package com.example.car_rental_sys.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Enterprise Fleet Vehicle Entity.
 */
public class Vehicle implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer vehicleId;
    private Integer categoryId;
    private String categoryName; // Denormalized for display/UI convenience
    private String brand;
    private String model;
    private Integer year;
    private String licensePlate;
    private String color;
    private Integer seats;
    private TransmissionType transmission;
    private FuelType fuelType;
    private BigDecimal dailyRate;
    private Integer mileage;
    private VehicleStatus status;
    private String imagePath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Vehicle() {
        this.seats = 5;
        this.transmission = TransmissionType.AUTOMATIC;
        this.fuelType = FuelType.PETROL;
        this.status = VehicleStatus.AVAILABLE;
        this.dailyRate = BigDecimal.ZERO;
        this.mileage = 0;
    }

    public Vehicle(String brand, String model, Integer year, String licensePlate,
                   BigDecimal dailyRate, VehicleCategory category) {
        this();
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.licensePlate = licensePlate;
        this.dailyRate = dailyRate;
        if (category != null) {
            this.categoryId = category.getCategoryId();
            this.categoryName = category.getName();
        }
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public TransmissionType getTransmission() {
        return transmission;
    }

    public void setTransmission(TransmissionType transmission) {
        this.transmission = transmission;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDisplayName() {
        return (brand != null ? brand : "") + " " + (model != null ? model : "") + " (" + (year != null ? year : "") + ")";
    }

    public boolean isAvailable() {
        return VehicleStatus.AVAILABLE.equals(this.status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle vehicle)) return false;
        return Objects.equals(vehicleId, vehicle.vehicleId) && Objects.equals(licensePlate, vehicle.licensePlate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicleId, licensePlate);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleId=" + vehicleId +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", licensePlate='" + licensePlate + '\'' +
                ", dailyRate=" + dailyRate +
                ", status=" + status +
                '}';
    }
}
