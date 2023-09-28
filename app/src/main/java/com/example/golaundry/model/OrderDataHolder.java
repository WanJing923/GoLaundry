package com.example.golaundry.model;

import androidx.lifecycle.ViewModel;

public class OrderDataHolder extends ViewModel {
    private OrderModel orderData;

    public OrderModel getOrderData() {
        return orderData;
    }

    public void setOrderData(OrderModel orderData) {
        this.orderData = orderData;
    }
}
