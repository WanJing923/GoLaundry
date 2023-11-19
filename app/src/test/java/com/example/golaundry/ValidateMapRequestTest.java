package com.example.golaundry;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.golaundry.test.ValidateMapRequest;

import org.junit.jupiter.api.Test;

public class ValidateMapRequestTest {
    @Test
    void validateCameraRequest() {
        assertAll(() -> assertEquals("Map Request Not Allowed", new ValidateMapRequest().validateMapRequest("")),
                () -> assertEquals("Invalid Map Request", new ValidateMapRequest().validateMapRequest(" ")),
                () -> assertEquals("Map Request Allowed", new ValidateMapRequest().validateMapRequest("Allowed"))
        );
    }
}