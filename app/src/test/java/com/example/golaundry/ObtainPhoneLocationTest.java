package com.example.golaundry;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.golaundry.test.ObtainPhoneLocation;

import org.junit.jupiter.api.Test;

public class ObtainPhoneLocationTest {
    @Test
    void obtainPhoneLocation() {
        assertAll(
                //Mock no location permission
                () -> assertEquals("No location permission", new ObtainPhoneLocation().obtainPhoneLocation(false, false)),
                //Mock error happened when obtaining location
                () -> assertEquals("Failed to obtain location", new ObtainPhoneLocation().obtainPhoneLocation(true, true)),
                //Mock location obtained successfully
                () -> assertEquals("Bayan Lepas", new ObtainPhoneLocation().obtainPhoneLocation(true, false))
        );
    }
}