package com.example.golaundry.test;

public class ValidateMapRequest {
    public String validateMapRequest(String mapRequest) {

        if (mapRequest.isEmpty()) {
            return "Map Request Not Allowed";
        } else if (mapRequest.contains(" ")) {
            return "Invalid Map Request";
        }

        return "Map Request Allowed";
    }
}