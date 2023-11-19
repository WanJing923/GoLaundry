package com.example.golaundry;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.golaundry.test.UpdateUserProfile;

import org.junit.jupiter.api.Test;

public class UpdateUserProfileTest {
    @Test
    void updateUserProfile() {
        assertAll(()->assertEquals("Username Cannot be Empty.", new UpdateUserProfile().updateUserProfile("","38574637","male")),
                () -> assertEquals("Username Cannot have Space.", new UpdateUserProfile().updateUserProfile("customer 2","38574637","male")),
                () -> assertEquals("Username Cannot have Space.", new UpdateUserProfile().updateUserProfile("customer2 ","38574637","male")),
                () -> assertEquals("Phone Number Cannot be Empty.", new UpdateUserProfile().updateUserProfile("customer2","","female")),
                () -> assertEquals("Phone Number Length Can Not Less Than 7 and More Than 9.", new UpdateUserProfile().updateUserProfile("customer2","52312","female")),
                () -> assertEquals("Phone Number Length Can Not Less Than 7 and More Than 9.", new UpdateUserProfile().updateUserProfile("customer2","5232132112312","female")),
                () -> assertEquals("Gender Cannot Be Empty.", new UpdateUserProfile().updateUserProfile("customer2","38574637","")),
                () -> assertEquals("Gender not match.", new UpdateUserProfile().updateUserProfile("customer2","38574637","man")),
                () -> assertEquals("Success", new UpdateUserProfile().updateUserProfile("customer2","38574637","male")),
                () -> assertEquals("Success", new UpdateUserProfile().updateUserProfile("customer2","38574637","female"))
        );
    }
}