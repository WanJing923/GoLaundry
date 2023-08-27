package com.example.golaundry.model;

public class CurrentMembershipModel {
    private String monthYear;
    private String monthlyTopUp;

    public CurrentMembershipModel(String monthYear, String monthlyTopUp) {
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

    public String getMonthlyTopUp() {
        return monthlyTopUp;
    }

    public void setMonthlyTopUp(String monthlyTopUp) {
        this.monthlyTopUp = monthlyTopUp;
    }
}
