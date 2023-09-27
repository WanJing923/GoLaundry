package com.example.golaundry.model;

public class OrderStatusModel {
    private String dateTime;
    private String statusContent;

    public OrderStatusModel() {
    }

    public OrderStatusModel(String dateTime, String statusContent) {
        this.dateTime = dateTime;
        this.statusContent = statusContent;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatusContent() {
        return statusContent;
    }

    public void setStatusContent(String statusContent) {
        this.statusContent = statusContent;
    }
}
