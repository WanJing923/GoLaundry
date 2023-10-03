package com.example.golaundry.model;

import java.io.Serializable;
import java.util.Map;

public class OrderModel implements Serializable {

    private String laundryId;
    private String userId;
    private Map<String, Integer> selectedServices;
    private String noteToLaundry;
    private String riderId;
    private Map<String, String> addressInfo;
    private String dateTime;
    private String currentStatus;
    private double laundryFee;
    private String membershipDiscount;
    private double deliveryFee;
    private double totalFee;
    private String pickUpDate;
    private String noteToRider;
    private double distanceBetweenUserLaundry;

    public OrderModel() {
    }

    public OrderModel(String laundryId, String userId, Map<String, Integer> selectedServices, String noteToLaundry, String riderId, Map<String, String> addressInfo, String dateTime, String currentStatus, double laundryFee, String membershipDiscount, double deliveryFee, double totalFee, String pickUpDate, String noteToRider, double distanceBetweenUserLaundry) {
        this.laundryId = laundryId;
        this.userId = userId;
        this.selectedServices = selectedServices;
        this.noteToLaundry = noteToLaundry;
        this.riderId = riderId;
        this.addressInfo = addressInfo;
        this.dateTime = dateTime;
        this.currentStatus = currentStatus;
        this.laundryFee = laundryFee;
        this.membershipDiscount = membershipDiscount;
        this.deliveryFee = deliveryFee;
        this.totalFee = totalFee;
        this.pickUpDate = pickUpDate;
        this.noteToRider = noteToRider;
        this.distanceBetweenUserLaundry = distanceBetweenUserLaundry;
    }

    public double getDistanceBetweenUserLaundry() {
        return distanceBetweenUserLaundry;
    }

    public void setDistanceBetweenUserLaundry(double distanceBetweenUserLaundry) {
        this.distanceBetweenUserLaundry = distanceBetweenUserLaundry;
    }

    public String getLaundryId() {
        return laundryId;
    }

    public void setLaundryId(String laundryId) {
        this.laundryId = laundryId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String currentUserId) {
        this.userId = userId;
    }

    public Map<String, Integer> getSelectedServices() {
        return selectedServices;
    }

    public void setSelectedServices(Map<String, Integer> selectedServices) {
        this.selectedServices = selectedServices;
    }

    public String getNoteToLaundry() {
        return noteToLaundry;
    }

    public void setNoteToLaundry(String noteToLaundry) {
        this.noteToLaundry = noteToLaundry;
    }

    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }

    public Map<String, String> getAddressInfo() {
        return addressInfo;
    }

    public void setAddressInfo(String info, Map<String, String> addressInfo) {
        this.addressInfo = addressInfo;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public double getLaundryFee() {
        return laundryFee;
    }

    public void setLaundryFee(double laundryFee) {
        this.laundryFee = laundryFee;
    }

    public String getMembershipDiscount() {
        return membershipDiscount;
    }

    public void setMembershipDiscount(String membershipDiscount) {
        this.membershipDiscount = membershipDiscount;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public double getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(double totalFee) {
        this.totalFee = totalFee;
    }

    public String getPickUpDate() {
        return pickUpDate;
    }

    public void setPickUpDate(String pickUpDate) {
        this.pickUpDate = pickUpDate;
    }

    public String getNoteToRider() {
        return noteToRider;
    }

    public void setNoteToRider(String noteToRider) {
        this.noteToRider = noteToRider;
    }
}
