package com.example.golaundry.model;

public class RateModel {
    private String rateId;
    private String userId;
    private String riderId;
    private String orderId;
    private String dateTime;
    private double rateToLaundry;
    private String commentToLaundry;
    private double rateToRider;
    private String commentToRider;

    public RateModel() {
    }

    public RateModel(String rateId, String userId, String riderId, String orderId, String dateTime, double rateToLaundry, String commentToLaundry, double rateToRider, String commentToRider) {
        this.rateId = rateId;
        this.userId = userId;
        this.riderId = riderId;
        this.orderId = orderId;
        this.dateTime = dateTime;
        this.rateToLaundry = rateToLaundry;
        this.commentToLaundry = commentToLaundry;
        this.rateToRider = rateToRider;
        this.commentToRider = commentToRider;
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

    public double getRateToLaundry() {
        return rateToLaundry;
    }

    public void setRateToLaundry(double rateToLaundry) {
        this.rateToLaundry = rateToLaundry;
    }

    public String getCommentToLaundry() {
        return commentToLaundry;
    }

    public void setCommentToLaundry(String commentToLaundry) {
        this.commentToLaundry = commentToLaundry;
    }

    public double getRateToRider() {
        return rateToRider;
    }

    public void setRateToRider(double rateToRider) {
        this.rateToRider = rateToRider;
    }

    public String getCommentToRider() {
        return commentToRider;
    }

    public void setCommentToRider(String commentToRider) {
        this.commentToRider = commentToRider;
    }
}
