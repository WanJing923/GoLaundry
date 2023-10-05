package com.example.golaundry.model;


public class ServiceItem {
    private String key;
    private int value;

    public ServiceItem() {
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public ServiceItem(String key, int value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }
}
