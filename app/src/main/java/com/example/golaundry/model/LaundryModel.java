package com.example.golaundry.model;

import java.io.Serializable;

public class LaundryModel implements Serializable {
    private String laundryId;
    private String shopName;
    private String contactNo;
    private String emailAddress;
    private String address;
    private String addressDetails;
    private String businessLicensePhoto;
    private String fullName;
    private String phoneNo;
    private String icNo;
    private String registerDateTime;
    private String status;
    private String userType;
    private boolean notification;
    private boolean setup;
    private boolean isBreak;
    private double balance;
    private float ratingsAverage;
    private boolean isNew;

    public LaundryModel() {
    }

    public LaundryModel(String laundryId, String shopName, String contactNo, String emailAddress, String address, String addressDetails, String businessLicensePhoto, String fullName, String phoneNo, String icNo, String registerDateTime, String status, String userType, boolean notification, boolean setup, boolean isBreak, double balance, float ratingsAverage, boolean isNew) {
        this.laundryId = laundryId;
        this.shopName = shopName;
        this.contactNo = contactNo;
        this.emailAddress = emailAddress;
        this.address = address;
        this.addressDetails = addressDetails;
        this.businessLicensePhoto = businessLicensePhoto;
        this.fullName = fullName;
        this.phoneNo = phoneNo;
        this.icNo = icNo;
        this.registerDateTime = registerDateTime;
        this.status = status;
        this.userType = userType;
        this.notification = notification;
        this.setup = setup;
        this.isBreak = isBreak;
        this.balance = balance;
        this.ratingsAverage = ratingsAverage;
        this.isNew = isNew;
    }

    public float getRatingsAverage() {
        return ratingsAverage;
    }

    public void setRatingsAverage(float ratingsAverage) {
        this.ratingsAverage = ratingsAverage;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getLaundryId() {
        return laundryId;
    }

    public void setLaundryId(String laundryId) {
        this.laundryId = laundryId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressDetails() {
        return addressDetails;
    }

    public void setAddressDetails(String addressDetails) {
        this.addressDetails = addressDetails;
    }

    public String getBusinessLicensePhoto() {
        return businessLicensePhoto;
    }

    public void setBusinessLicensePhoto(String businessLicensePhoto) {
        this.businessLicensePhoto = businessLicensePhoto;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getIcNo() {
        return icNo;
    }

    public void setIcNo(String icNo) {
        this.icNo = icNo;
    }

    public String getRegisterDateTime() {
        return registerDateTime;
    }

    public void setRegisterDateTime(String registerDateTime) {
        this.registerDateTime = registerDateTime;
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

    public boolean getNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public boolean getSetup() {
        return setup;
    }

    public void setSetup(boolean setup) {
        this.setup = setup;
    }

    public boolean getIsBreak() {
        return isBreak;
    }

    public void setIsBreak(boolean isBreak) {
        this.isBreak = isBreak;
    }

    public boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }
}
