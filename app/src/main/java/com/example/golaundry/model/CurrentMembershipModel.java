package com.example.golaundry.model;

public class CurrentMembershipModel {
    private String monthYear;
    private double monthlyTopUp;

    public CurrentMembershipModel(String monthYear, double monthlyTopUp) {
        this.monthYear = monthYear;
        this.monthlyTopUp = monthlyTopUp;
    }

    public CurrentMembershipModel() {

    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public double getMonthlyTopUp() {
        return monthlyTopUp;
    }

    public void setMonthlyTopUp(double monthlyTopUp) {
        this.monthlyTopUp = monthlyTopUp;
    }
}
