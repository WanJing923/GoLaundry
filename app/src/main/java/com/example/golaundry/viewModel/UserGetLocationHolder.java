package com.example.golaundry.viewModel;

import androidx.lifecycle.ViewModel;

public class UserGetLocationHolder extends ViewModel {
    private String fullAddress;
    private String area;
    private boolean hasGetCurrentAreaBeenCalled = false;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public boolean hasGetCurrentAreaBeenCalled() {
        return hasGetCurrentAreaBeenCalled;
    }

    public void setGetCurrentAreaCalled(boolean called) {
        hasGetCurrentAreaBeenCalled = called;
    }
}