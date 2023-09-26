package com.example.golaundry.model;

public class AddressModel {
    private String name;
    private String address;
    private String details;
    private boolean defaultAddress;

    public AddressModel() {
    }

    public AddressModel(String name, String address, String details, boolean defaultAddress) {
        this.name = name;
        this.address = address;
        this.details = details;
        this.defaultAddress = defaultAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }
}
