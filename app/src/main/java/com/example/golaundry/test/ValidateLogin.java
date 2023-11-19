package com.example.golaundry.test;

public class ValidateLogin {
    public String validateLogin(String password) {

        if (password.isEmpty()) {
            return "Password cannot be empty.";

        } else if (password.length() < 9){
            return "Password length cannot less than 9";

        }

        return "Success";
    }
}