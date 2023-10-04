package com.example.golaundry.model;

import androidx.lifecycle.ViewModel;

public class RiderFindOrderHolder extends ViewModel {
    private OrderModel orderData;
    private double distance;

    public RiderFindOrderHolder(OrderModel orderData, double distance) {
        this.orderData = orderData;
        this.distance = distance;
    }

    public RiderFindOrderHolder() {
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public OrderModel getOrderData() {
        return orderData;
    }

    public void setOrderData(OrderModel orderData) {
        this.orderData = orderData;
    }
}
