package com.example.golaundry.viewModel;
import com.example.golaundry.model.LaundryModel;
import com.example.golaundry.model.OrderModel;

public class OrderLocationDataHolder {

    private static OrderLocationDataHolder instance;

    private OrderModel orderData;
    private LaundryModel laundryData;
    private String selectedDate;
    private String noteToRider;
    private String noteToLaundry;

    private OrderLocationDataHolder() {
    }

    public static OrderLocationDataHolder getInstance() {
        if (instance == null) {
            instance = new OrderLocationDataHolder();
        }
        return instance;
    }

    public LaundryModel getLaundryData() {
        return laundryData;
    }

    public void setLaundryData(LaundryModel laundryData) {
        this.laundryData = laundryData;
    }

    public void setOrderData(OrderModel orderData) {
        this.orderData = orderData;
    }

    public OrderModel getOrderData() {
        return orderData;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setNoteToRider(String noteToRider) {
        this.noteToRider = noteToRider;
    }

    public String getNoteToRider() {
        return noteToRider;
    }

    public void setNoteToLaundry(String noteToLaundry) {
        this.noteToLaundry = noteToLaundry;
    }

    public String getNoteToLaundry() {
        return noteToLaundry;
    }
}



