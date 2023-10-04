package com.example.golaundry.model;

public class CashOutModel {

    private String dateTime;
    private double amount;
    private double currentBalance;

    public CashOutModel(String dateTime, double amount, double currentBalance) {
        this.dateTime = dateTime;
        this.amount = amount;
        this.currentBalance = currentBalance;
    }

    public CashOutModel() {
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }
}
