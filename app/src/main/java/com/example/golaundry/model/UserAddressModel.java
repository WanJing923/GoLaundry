package com.example.golaundry.model;

public class UserAddressModel {
    private String name;
    private String addressDetails;
    private String address;
    private boolean defaultAddress;

    public UserAddressModel() {
    }

    public UserAddressModel(String name, String addressDetails, String address, boolean defaultAddress) {
        this.name = name;
        this.addressDetails = addressDetails;
        this.address = address;
        this.defaultAddress = defaultAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddressDetails() {
        return addressDetails;
    }

    public void setAddressDetails(String addressDetails) {
        this.addressDetails = addressDetails;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }
}
