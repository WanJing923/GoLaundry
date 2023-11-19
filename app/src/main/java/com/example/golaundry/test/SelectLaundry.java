package com.example.golaundry.test;

import java.util.Arrays;
import java.util.List;

public class SelectLaundry {
    List<String> laundry = Arrays.asList("QQ Laundry", "Dry Cleaning Laundry","AngDobi Laundry");


    public SelectLaundry() {
    }

    public String selectCourier(String selectedLaundry){
        if(laundry.contains(selectedLaundry)){
            return "Success";
        }

        if (!laundry.contains(selectedLaundry)) {
            return "Invalid courier";
        }

        return "Unknown courier";
    }
}