package com.example.golaundry.model;

public class UserModel {
    private String userId;
    private String fullName;
    private String gender;
    private String icNo;
    private String phoneNo;
    private String emailAddress;
    private String status;
    private String userType;
    private String membershipRate;
    private String avatar;
    private double balance;
    private boolean notification;

    public UserModel(String userId, String fullName, String gender, String icNo, String phoneNo, String emailAddress, String status, String userType, String membershipRate, String avatar, double balance, boolean notification) {
        this.userId = userId;
        this.fullName = fullName;
        this.gender = gender;
        this.icNo = icNo;
        this.phoneNo = phoneNo;
        this.emailAddress = emailAddress;
        this.status = status;
        this.userType = userType;
        this.membershipRate = membershipRate;
        this.avatar = avatar;
        this.balance = balance;
        this.notification = notification;
    }

    public UserModel() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getMembershipRate() {
        return membershipRate;
    }

    public void setMembershipRate(String membershipRate) {
        this.membershipRate = membershipRate;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean getNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }
}
