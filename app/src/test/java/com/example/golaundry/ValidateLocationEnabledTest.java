package com.example.golaundry;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.golaundry.test.ValidateLocationEnabled;

import org.junit.jupiter.api.Test;

public class ValidateLocationEnabledTest {
    @Test
    void validateLocationEnabled() {
        assertAll(()->assertEquals("GPS cannot be null.", new ValidateLocationEnabled().validateLocationEnabled(null)),
                ()->assertEquals("GPS is not enabled.", new ValidateLocationEnabled().validateLocationEnabled(false)),
                ()->assertEquals("Success", new ValidateLocationEnabled().validateLocationEnabled(true))
        );
    }
}