package com.example.golaundry;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.golaundry.test.SelectLaundry;

import org.junit.jupiter.api.Test;

public class SelectLaundryTest {

    @Test
    void selectCourier() {
        assertAll(
                () -> assertEquals("Invalid courier", new SelectLaundry().selectCourier("ABC")),
                () -> assertEquals("Success", new SelectLaundry().selectCourier("AngDobi Laundry")),
                () -> assertEquals("Success", new SelectLaundry().selectCourier("Dry Cleaning Laundry")),
                () -> assertEquals("Success", new SelectLaundry().selectCourier("QQ Laundry"))
        );
    }
}