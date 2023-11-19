package com.example.golaundry;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.golaundry.test.ValidateQRCode;

import org.junit.jupiter.api.Test;

public class ValidateQRCodeTest {
    @Test
    void validateQRCode() {
        assertAll(
                //Mock if the QR code not scanning
                () -> assertEquals("No QR code scanned",
                        new ValidateQRCode().validateQRCode(null)),
                //Mock if the QR code is invalid
                () -> assertEquals("Invalid QR code",
                        new ValidateQRCode().validateQRCode("")),
                //Mock if the QR code scan successful
                () -> assertEquals("QR Code Scan Successfully",
                        new ValidateQRCode().validateQRCode("5b4b1feb-de6f-4b2f-a554-80a0cea5e9cd"))
        );
    }
}