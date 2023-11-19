package com.example.golaundry.test;

public class ValidateLocationEnabled {
    public String validateLocationEnabled(Boolean gps_enabled){

        if (gps_enabled == null) {
            return "GPS cannot be null.";
        } else if (gps_enabled == false) {
            return "GPS is not enabled.";
        } else if (!gps_enabled){
            return "Network is not enabled.";
        }
        return "Success";
    }
}