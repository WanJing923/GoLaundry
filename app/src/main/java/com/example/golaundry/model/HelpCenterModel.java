package com.example.golaundry.model;

public class HelpCenterModel {
    private String emailAddress;
    private String title;
    private String message;
    private String status;

    public HelpCenterModel(String emailAddress, String title, String message, String status) {
        this.emailAddress = emailAddress;
        this.title = title;
        this.message = message;
        this.status = status;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
