package com.example.golaundry.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AddressModel implements Parcelable {
    private String addressId;
    private String name;
    private String address;
    private String details;
    private boolean defaultAddress;

    public AddressModel() {
        // Default constructor
    }

    public AddressModel(String addressId,String name, String address, String details, boolean defaultAddress) {
        this.addressId = addressId;
        this.name = name;
        this.address = address;
        this.details = details;
        this.defaultAddress = defaultAddress;
    }

    protected AddressModel(Parcel in) {
        addressId = in.readString();
        name = in.readString();
        address = in.readString();
        details = in.readString();
        defaultAddress = in.readByte() != 0;
    }

    public static final Creator<AddressModel> CREATOR = new Creator<AddressModel>() {
        @Override
        public AddressModel createFromParcel(Parcel in) {
            return new AddressModel(in);
        }

        @Override
        public AddressModel[] newArray(int size) {
            return new AddressModel[size];
        }
    };

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
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(addressId);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(details);
        dest.writeByte((byte) (defaultAddress ? 1 : 0));
    }
}
