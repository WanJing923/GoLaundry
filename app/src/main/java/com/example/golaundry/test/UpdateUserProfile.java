package com.example.golaundry.test;

public class UpdateUserProfile {
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public String updateUserProfile(String name, String phonenumber, String gender){


        if (name.isEmpty()) {
            return "Username Cannot be Empty.";

        } else if (name.contains(" ")) {
            return "Username Cannot have Space.";

        } else if (phonenumber.isEmpty()) {
            return "Phone Number Cannot be Empty.";

        } else if (phonenumber.length() > 9 || phonenumber.length() < 8 ) {
            return "Phone Number Length Can Not Less Than 7 and More Than 9.";

        } else if (gender.isEmpty()) {
            return "Gender Cannot Be Empty.";

        } else if (!gender.equals("male") && !gender.equals("female")){
            return "Gender not match.";

        }

        return "Success";
    }
}