package com.example.golaundry.test;

public class ValidatePasswordEnteredByUser {
    public ValidatePasswordEnteredByUser() {
    }

    public String validatePasswordEnteredByUser(String password) {
        if(password==null){
            return "Password cannot be null";
        }

        if (password.isEmpty() || password.trim().isEmpty()) {
            return "Password cannot be empty";
        }

        if (password.contains(" ")) {
            return "Password cannot contain spaces";
        }

        if (password.length() < 9) {
            return "Password length cannot be less than 9";
        }

        return "Success";
    }
}