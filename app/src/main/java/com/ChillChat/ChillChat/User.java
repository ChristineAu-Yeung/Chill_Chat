package com.ChillChat.ChillChat;

import java.util.Calendar;
import java.util.Objects;

public class User {
    String dateRegistered;
    String email;
    String firstName;
    String userID;
//        String imageUrl; // For the future

    /**
     * Object that represents each user
     *
     *
     */
    public User(String sDateRegistered, String sEmail, String sFirstName, String sUserID) {
        dateRegistered = sDateRegistered;
        email = sEmail;
        firstName = sFirstName;
        userID = sUserID;
    }

    public String getDateRegistered() {
        return dateRegistered;
    }

    public String getEmail(){
        return userID;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getUserID(){
        return userID;
    }
}
