package com.example.car_rental_sys.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * System audit log entity for security tracking and compliance.
 */
public class SystemLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer logId;
    private Integer userId;
    private String userName; // View property
    private String actionType;
    private String details;
    private String ipAddress;
    private LocalDateTime timestamp;

    public SystemLog() {
        this.timestamp = LocalDateTime.now();
    }

    public SystemLog(Integer userId, String actionType, String details, String ipAddress) {
        this();
        this.userId = userId;
        this.actionType = actionType;
        this.details = details;
        this.ipAddress = ipAddress;
    }

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SystemLog systemLog)) return false;
        return Objects.equals(logId, systemLog.logId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logId);
    }

    @Override
    public String toString() {
        return "SystemLog{" +
                "logId=" + logId +
                ", userId=" + userId +
                ", actionType='" + actionType + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
