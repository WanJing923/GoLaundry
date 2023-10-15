package com.example.golaundry.model;

import java.io.Serializable;

public class RateLaundryModel implements Serializable {

    private String rateLaundryId;
    private String userId;
    private String riderId;
    private String orderId;
    private String dateTime;
    private float rateToLaundry;
    private String commentToLaundry;
    private String laundryId;

    public RateLaundryModel() {
    }

    public RateLaundryModel(String rateLaundryId, String userId, String riderId, String orderId, String dateTime, float rateToLaundry, String commentToLaundry, String laundryId) {
        this.rateLaundryId = rateLaundryId;
        this.userId = userId;
        this.riderId = riderId;
        this.orderId = orderId;
        this.dateTime = dateTime;
        this.rateToLaundry = rateToLaundry;
        this.commentToLaundry = commentToLaundry;
        this.laundryId = laundryId;
    }

    public String getRateLaundryId() {
        return rateLaundryId;
    }

    public void setRateLaundryId(String rateLaundryId) {
        this.rateLaundryId = rateLaundryId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public float getRateToLaundry() {
        return rateToLaundry;
    }

    public void setRateToLaundry(float rateToLaundry) {
        this.rateToLaundry = rateToLaundry;
    }

    public String getCommentToLaundry() {
        return commentToLaundry;
    }

    public void setCommentToLaundry(String commentToLaundry) {
        this.commentToLaundry = commentToLaundry;
    }

    public String getLaundryId() {
        return laundryId;
    }

    public void setLaundryId(String laundryId) {
        this.laundryId = laundryId;
    }
}
