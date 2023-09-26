package com.example.golaundry.model;

import java.io.Serializable;
import java.util.Map;

public class OrderModel implements Serializable {

    private String laundryId;
    private String currentUserId;
    private Map<String, Integer> selectedServices;
    private String note;
    private String riderId;
    private Map<String, String> addressInfo;
    private String dateTime;
    private String currentStatus;
    private double laundryFee;
    private String membershipDiscount;
    private double deliveryFee;
    private double totalFee;

    public OrderModel() {
    }

    public OrderModel(String laundryId, String currentUserId, Map<String, Integer> selectedServices, String note, String riderId, Map<String, String> addressInfo, String dateTime, String currentStatus, double laundryFee, String membershipDiscount, double deliveryFee, double totalFee) {
        this.laundryId = laundryId;
        this.currentUserId = currentUserId;
        this.selectedServices = selectedServices;
        this.note = note;
        this.riderId = riderId;
        this.addressInfo = addressInfo;
        this.dateTime = dateTime;
        this.currentStatus = currentStatus;
        this.laundryFee = laundryFee;
        this.membershipDiscount = membershipDiscount;
        this.deliveryFee = deliveryFee;
        this.totalFee = totalFee;
    }

    public String getLaundryId() {
        return laundryId;
    }

    public void setLaundryId(String laundryId) {
        this.laundryId = laundryId;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public Map<String, Integer> getSelectedServices() {
        return selectedServices;
    }

    public void setSelectedServices(Map<String, Integer> selectedServices) {
        this.selectedServices = selectedServices;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public void setAddressInfo(Map<String, String> addressInfo) {
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
}