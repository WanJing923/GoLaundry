package com.example.golaundry.test;

public class ValidateQRCode {
    public String validateQRCode(String QRCodeValue) {

        if (QRCodeValue == null) {
            return "No QR code scanned";
        }

        if (QRCodeValue.isEmpty()) {
            return "Invalid QR code";
        }

        return "QR Code Scan Successfully";
    }
}