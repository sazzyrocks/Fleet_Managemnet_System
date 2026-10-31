package com.example.car_rental_sys.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Real-time diagnostic and telemetry entity for fleet health monitoring.
 */
public class VehicleDiagnostic implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer diagId;
    private Integer vehicleId;
    private BigDecimal engineTemperature;
    private BigDecimal tirePressureFrontLeft;
    private BigDecimal tirePressureFrontRight;
    private BigDecimal tirePressureRearLeft;
    private BigDecimal tirePressureRearRight;
    private Integer batteryHealthPercent;
    private Integer fuelLevelPercent;
    private LocalDateTime recordedAt;

    public VehicleDiagnostic() {
        this.engineTemperature = new BigDecimal("90.0");
        this.tirePressureFrontLeft = new BigDecimal("32.0");
        this.tirePressureFrontRight = new BigDecimal("32.0");
        this.tirePressureRearLeft = new BigDecimal("32.0");
        this.tirePressureRearRight = new BigDecimal("32.0");
        this.batteryHealthPercent = 100;
        this.fuelLevelPercent = 100;
        this.recordedAt = LocalDateTime.now();
    }

    public VehicleDiagnostic(Integer vehicleId, BigDecimal engineTemperature, Integer batteryHealthPercent, Integer fuelLevelPercent) {
        this();
        this.vehicleId = vehicleId;
        this.engineTemperature = engineTemperature;
        this.batteryHealthPercent = batteryHealthPercent;
        this.fuelLevelPercent = fuelLevelPercent;
    }

    public Integer getDiagId() {
        return diagId;
    }

    public void setDiagId(Integer diagId) {
        this.diagId = diagId;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public BigDecimal getEngineTemperature() {
        return engineTemperature;
    }

    public void setEngineTemperature(BigDecimal engineTemperature) {
        this.engineTemperature = engineTemperature;
    }

    public BigDecimal getTirePressureFrontLeft() {
        return tirePressureFrontLeft;
    }

    public void setTirePressureFrontLeft(BigDecimal tirePressureFrontLeft) {
        this.tirePressureFrontLeft = tirePressureFrontLeft;
    }

    public BigDecimal getTirePressureFrontRight() {
        return tirePressureFrontRight;
    }

    public void setTirePressureFrontRight(BigDecimal tirePressureFrontRight) {
        this.tirePressureFrontRight = tirePressureFrontRight;
    }

    public BigDecimal getTirePressureRearLeft() {
        return tirePressureRearLeft;
    }

    public void setTirePressureRearLeft(BigDecimal tirePressureRearLeft) {
        this.tirePressureRearLeft = tirePressureRearLeft;
    }

    public BigDecimal getTirePressureRearRight() {
        return tirePressureRearRight;
    }

    public void setTirePressureRearRight(BigDecimal tirePressureRearRight) {
        this.tirePressureRearRight = tirePressureRearRight;
    }

    public Integer getBatteryHealthPercent() {
        return batteryHealthPercent;
    }

    public void setBatteryHealthPercent(Integer batteryHealthPercent) {
        this.batteryHealthPercent = batteryHealthPercent;
    }

    public Integer getFuelLevelPercent() {
        return fuelLevelPercent;
    }

    public void setFuelLevelPercent(Integer fuelLevelPercent) {
        this.fuelLevelPercent = fuelLevelPercent;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    public boolean isEngineOverheating() {
        return engineTemperature != null && engineTemperature.compareTo(new BigDecimal("105.0")) > 0;
    }

    public boolean isLowFuel() {
        return fuelLevelPercent != null && fuelLevelPercent < 15;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VehicleDiagnostic that)) return false;
        return Objects.equals(diagId, that.diagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(diagId);
    }

    @Override
    public String toString() {
        return "VehicleDiagnostic{" +
                "diagId=" + diagId +
                ", vehicleId=" + vehicleId +
                ", engineTemp=" + engineTemperature + "°C" +
                ", battery=" + batteryHealthPercent + "%" +
                ", fuel=" + fuelLevelPercent + "%" +
                '}';
    }
}
