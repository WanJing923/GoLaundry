package com.example.golaundry.model;

public class User {

    private String fullName;
    private String gender;
    private String icNo;
    private String phoneNo;
    private String emailAddress;
    private String status;

    public User(String fullName, String gender, String icNo, String phoneNo, String emailAddress, String status) {
        this.fullName = fullName;
        this.gender = gender;
        this.icNo = icNo;
        this.phoneNo = phoneNo;
        this.emailAddress = emailAddress;
        this.status = status;
    }

    public User() {

    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIcNo() {
        return icNo;
    }

    public void setIcNo(String icNo) {
        this.icNo = icNo;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getstatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
