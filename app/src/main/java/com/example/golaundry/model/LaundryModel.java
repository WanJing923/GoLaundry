package com.example.golaundry.model;

public class LaundryModel {

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

    public LaundryModel() {
    }

    public LaundryModel(String shopName, String contactNo, String emailAddress, String address, String addressDetails, String businessLicensePhoto, String fullName, String phoneNo, String icNo, String registerDateTime, String status, String userType, boolean notification) {
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
}
