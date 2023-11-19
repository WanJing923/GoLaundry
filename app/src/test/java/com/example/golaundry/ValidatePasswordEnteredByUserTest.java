package com.example.golaundry;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.golaundry.test.ValidatePasswordEnteredByUser;

import org.junit.jupiter.api.Test;

public class ValidatePasswordEnteredByUserTest {

    @Test
    void validatePasswordEnteredByUser() {
        assertAll(
                //Mock no password is provided
                () -> assertEquals("Password cannot be null", new ValidatePasswordEnteredByUser().validatePasswordEnteredByUser(null)),
                //Mock if user did not enter anything
                () -> assertEquals("Password cannot be empty", new ValidatePasswordEnteredByUser().validatePasswordEnteredByUser("")),
                //Password will be empty after removing all the spaces
                () -> assertEquals("Password cannot be empty", new ValidatePasswordEnteredByUser().validatePasswordEnteredByUser("  ")),
                //Mock if user enter spaces in their password
                () -> assertEquals("Password cannot contain spaces", new ValidatePasswordEnteredByUser().validatePasswordEnteredByUser("12 3456")),
                //Mock if the user didn't enter sufficient password length
                () -> assertEquals("Password length cannot be less than 9", new ValidatePasswordEnteredByUser().validatePasswordEnteredByUser("12345")),
                //Mock if the password enter by the user met the requirements
                () -> assertEquals("Success", new ValidatePasswordEnteredByUser().validatePasswordEnteredByUser("1234567890"))

        );
    }
}