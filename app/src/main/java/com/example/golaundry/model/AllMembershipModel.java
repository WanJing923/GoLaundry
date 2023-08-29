package com.example.golaundry.model;

public class AllMembershipModel {
    private double discount;
    private double monthlyTopUp;

    public AllMembershipModel(double discount, double monthlyTopUp) {
        this.discount = discount;
        this.monthlyTopUp = monthlyTopUp;
    }

    public AllMembershipModel() {

    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getMonthlyTopUp() {
        return monthlyTopUp;
    }

    public void setMonthlyTopUp(double monthlyTopUp) {
        this.monthlyTopUp = monthlyTopUp;
    }
}
