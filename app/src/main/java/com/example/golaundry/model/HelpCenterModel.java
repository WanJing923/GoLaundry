package com.example.golaundry.model;

public class HelpCenterModel {
    private String helpId;
    private String dateTime;
    private String emailAddress;
    private String title;
    private String message;
    private String status;

    public HelpCenterModel(String helpId, String dateTime, String emailAddress, String title, String message, String status) {
        this.helpId = helpId;
        this.dateTime = dateTime;
        this.emailAddress = emailAddress;
        this.title = title;
        this.message = message;
        this.status = status;
    }

    public HelpCenterModel() {
    }

    public String getHelpId() {
        return helpId;
    }

    public void setHelpId(String helpId) {
        this.helpId = helpId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
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
