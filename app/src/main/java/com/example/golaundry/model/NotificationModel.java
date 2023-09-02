package com.example.golaundry.model;

public class NotificationModel {

    private String title;
    private String content;
    private String dateTime;
    private String status;

    public NotificationModel(String title, String content, String dateTime, String status) {
        this.title = title;
        this.content = content;
        this.dateTime = dateTime;
        this.status = status;
    }

    public NotificationModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
