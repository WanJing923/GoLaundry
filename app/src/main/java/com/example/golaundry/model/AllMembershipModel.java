package com.example.golaundry.model;

public class AllMembershipModel {
    private int discount;
    private int monthlyTopUp;

    public AllMembershipModel(int discount, int monthlyTopUp) {
        this.discount = discount;
        this.monthlyTopUp = monthlyTopUp;
    }

    public AllMembershipModel() {

    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getMonthlyTopUp() {
        return monthlyTopUp;
    }

    public void setMonthlyTopUp(int monthlyTopUp) {
        this.monthlyTopUp = monthlyTopUp;
    }
}
