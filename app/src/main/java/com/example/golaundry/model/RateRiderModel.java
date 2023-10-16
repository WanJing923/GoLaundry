package com.example.golaundry.model;

import java.io.Serializable;

public class RateRiderModel implements Serializable {

    private String rateRiderId;
    private String userId;
    private String riderId;
    private String orderId;
    private String dateTime;
    private float rateToRider;
    private String commentToRider;
    private String laundryId;

    public RateRiderModel() {
    }

    public RateRiderModel(String rateRiderId, String userId, String riderId, String orderId, String dateTime, float rateToRider, String commentToRider, String laundryId) {
        this.rateRiderId = rateRiderId;
        this.userId = userId;
        this.riderId = riderId;
        this.orderId = orderId;
        this.dateTime = dateTime;
        this.rateToRider = rateToRider;
        this.commentToRider = commentToRider;
        this.laundryId = laundryId;
    }

    public String getRateRiderId() {
        return rateRiderId;
    }

    public void setRateRiderId(String rateRiderId) {
        this.rateRiderId = rateRiderId;
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

    public float getRateToRider() {
        return rateToRider;
    }

    public void setRateToRider(float rateToRider) {
        this.rateToRider = rateToRider;
    }

    public String getCommentToRider() {
        return commentToRider;
    }

    public void setCommentToRider(String commentToRider) {
        this.commentToRider = commentToRider;
    }

    public String getLaundryId() {
        return laundryId;
    }

    public void setLaundryId(String laundryId) {
        this.laundryId = laundryId;
    }
}
