package com.example.golaundry;

import static org.junit.jupiter.api.Assertions.*;

import com.example.golaundry.test.ValidateLogin;

import org.junit.jupiter.api.Test;

class ValidateLoginTest {

    @Test
    void validateLogin() {
        assertAll(() -> assertEquals("Password cannot be empty.", new ValidateLogin().validateLogin("")),
                () -> assertEquals("Password length cannot less than 9", new ValidateLogin().validateLogin("1234")),
                () -> assertEquals("Password length cannot less than 9", new ValidateLogin().validateLogin("123")),
                () -> assertEquals("Success", new ValidateLogin().validateLogin("123456789"))
        );
    }
}