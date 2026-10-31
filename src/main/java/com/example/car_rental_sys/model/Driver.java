package com.example.car_rental_sys.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Driver entity representing professional chauffeur personnel in the fleet.
 */
public class Driver implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer driverId;
    private Integer userId;
    private String driverName; // Convenience view property
    private String phone;      // Convenience view property
    private String licenseNumber;
    private Integer experienceYears;
    private BigDecimal rating;
    private DriverStatus status;
    private LocalDateTime createdAt;

    public Driver() {
        this.experienceYears = 1;
        this.rating = new BigDecimal("5.00");
        this.status = DriverStatus.AVAILABLE;
    }

    public Driver(Integer userId, String licenseNumber, Integer experienceYears) {
        this();
        this.userId = userId;
        this.licenseNumber = licenseNumber;
        this.experienceYears = experienceYears;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isAvailable() {
        return DriverStatus.AVAILABLE.equals(this.status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Driver driver)) return false;
        return Objects.equals(driverId, driver.driverId) && Objects.equals(licenseNumber, driver.licenseNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverId, licenseNumber);
    }

    @Override
    public String toString() {
        return "Driver{" +
                "driverId=" + driverId +
                ", userId=" + userId +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", rating=" + rating +
                ", status=" + status +
                '}';
    }
}
