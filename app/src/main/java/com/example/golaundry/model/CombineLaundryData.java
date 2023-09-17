package com.example.golaundry.model;

import java.util.ArrayList;
import java.util.List;

public class CombineLaundryData {

    private LaundryModel laundry;
    private LaundryShopModel shop;
    private List<LaundryServiceModel> serviceList;

    public CombineLaundryData(LaundryModel laundry, LaundryShopModel shop, List<LaundryServiceModel> serviceList) {
        this.laundry = laundry;
        this.shop = shop;
        this.serviceList = serviceList;
    }

    public LaundryModel getLaundry() {
        return laundry;
    }

    public void setLaundry(LaundryModel laundry) {
        this.laundry = laundry;
    }

    public LaundryShopModel getShop() {
        return shop;
    }

    public void setShop(LaundryShopModel shop) {
        this.shop = shop;
    }

    public List<LaundryServiceModel> getServiceList() {
        return serviceList;
    }

    public void setServiceList(ArrayList<LaundryServiceModel> serviceList) {
        this.serviceList = serviceList;
    }
}
