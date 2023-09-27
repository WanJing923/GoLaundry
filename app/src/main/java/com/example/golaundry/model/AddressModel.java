package com.example.golaundry.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class AddressModel implements Serializable {
    private String addressId;
    private String name;
    private String address;
    private String details;
    private boolean defaultAddress;

    public AddressModel() {
    }

    public AddressModel(String addressId,String name, String address, String details, boolean defaultAddress) {
        this.addressId = addressId;
        this.name = name;
        this.address = address;
        this.details = details;
        this.defaultAddress = defaultAddress;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getDetails() {
        return details;
    }

    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }
}
