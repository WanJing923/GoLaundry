package com.example.golaundry.model;

public class RiderModel {

    private String fullName;
    private String contactNo;
    private String emailAddress;
    private String plateNumber;
    private String facePhoto;
    private String drivingLicensePhoto;
    private String icNo;
    private String registerDateTime;
    private String status;

    public RiderModel(String fullName, String contactNo, String emailAddress, String plateNumber, String facePhoto, String drivingLicensePhoto, String icNo, String registerDateTime, String status) {
        this.fullName = fullName;
        this.contactNo = contactNo;
        this.emailAddress = emailAddress;
        this.plateNumber = plateNumber;
        this.facePhoto = facePhoto;
        this.drivingLicensePhoto = drivingLicensePhoto;
        this.icNo = icNo;
        this.registerDateTime = registerDateTime;
        this.status = status;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getFacePhoto() {
        return facePhoto;
    }

    public void setFacePhoto(String facePhoto) {
        this.facePhoto = facePhoto;
    }

    public String getDrivingLicensePhoto() {
        return drivingLicensePhoto;
    }

    public void setDrivingLicensePhoto(String drivingLicensePhoto) {
        this.drivingLicensePhoto = drivingLicensePhoto;
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
}
