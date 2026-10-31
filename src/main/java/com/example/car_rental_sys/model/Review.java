package com.example.car_rental_sys.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Customer review & rating entity.
 */
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer reviewId;
    private Integer orderId;
    private Integer userId;
    private String userName; // View property
    private Integer vehicleId;
    private Integer rating;  // 1 to 5 stars
    private String commentText;
    private LocalDateTime createdAt;

    public Review() {
        this.rating = 5;
        this.createdAt = LocalDateTime.now();
    }

    public Review(Integer orderId, Integer userId, Integer vehicleId, Integer rating, String commentText) {
        this();
        this.orderId = orderId;
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.rating = rating != null ? Math.max(1, Math.min(5, rating)) : 5;
        this.commentText = commentText;
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
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

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating != null ? Math.max(1, Math.min(5, rating)) : 5;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review review)) return false;
        return Objects.equals(reviewId, review.reviewId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewId);
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewId=" + reviewId +
                ", orderId=" + orderId +
                ", rating=" + rating +
                ", commentText='" + commentText + '\'' +
                '}';
    }
}
