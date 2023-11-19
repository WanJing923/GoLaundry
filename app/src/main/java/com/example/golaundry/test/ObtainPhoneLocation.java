package com.example.golaundry.test;

public class ObtainPhoneLocation {
    public String obtainPhoneLocation(boolean hasLocationPerMission, boolean mockError) {
        if (!hasLocationPerMission) {
            return "No location permission";
        }

        if (mockError) {
            return "Failed to obtain location";
        }

        return "Bayan Lepas";

    }
}