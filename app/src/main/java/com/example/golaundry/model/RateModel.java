package com.example.golaundry.model;

import java.io.Serializable;

public class RateModel implements Serializable {
    private String rateId;
    private String userId;
    private String riderId;
    private String orderId;
    private String dateTime;
    private float rateToLaundry;
    private String commentToLaundry;
    private float rateToRider;
    private String commentToRider;
    private String laundryId;

    public RateModel() {
    }

    public RateModel(String rateId, String userId, String riderId, String orderId, String dateTime, float rateToLaundry, String commentToLaundry, float rateToRider, String commentToRider, String laundryId) {
        this.rateId = rateId;
        this.userId = userId;
        this.riderId = riderId;
        this.orderId = orderId;
        this.dateTime = dateTime;
        this.rateToLaundry = rateToLaundry;
        this.commentToLaundry = commentToLaundry;
        this.rateToRider = rateToRider;
        this.commentToRider = commentToRider;
        this.laundryId = laundryId;
    }

    public String getRateId() {
        return rateId;
    }

    public void setRateId(String rateId) {
        this.rateId = rateId;
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