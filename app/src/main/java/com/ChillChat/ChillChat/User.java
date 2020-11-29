package com.ChillChat.ChillChat;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class User {
    private Date dateRegistered;
    private String email;
    private String firstName;
    private String userID;
//        String imageUrl; // For the future

    /**
     * Object that represents each user
     *
     */
    public User(Date sDateRegistered, String sEmail, String sFirstName, String sUserID) {
        dateRegistered = sDateRegistered;
        email = sEmail;
        firstName = sFirstName;
        userID = sUserID;
    }


    public User(Date sDateRegistered, String sEmail, String sFirstName, String sUserID, String imageUrl) {
        dateRegistered = sDateRegistered;
        email = sEmail;
        firstName = sFirstName;
        userID = sUserID;
    }

    public Date getDateRegistered() {
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
