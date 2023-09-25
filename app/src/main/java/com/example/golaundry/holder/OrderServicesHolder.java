package com.example.golaundry.holder;

public class OrderServicesHolder {
    private String name;
    private double price;
    private String quantity;
    private int userSelected;

    public OrderServicesHolder() {
    }

    public OrderServicesHolder(String name, double price, String quantity, int userSelected) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.userSelected = userSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public int getUserSelected() {
        return userSelected;
    }

    public void setUserSelected(int userSelected) {
        this.userSelected = userSelected;
    }
}
