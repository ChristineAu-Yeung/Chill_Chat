package com.ChillChat.ChillChat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.util.Date;

public class User {
    private Date dateRegistered;
    private String email;
    private String firstName;
    private String userID;
    private long age;
    private String bio;
    private String profileImage;

    /**
     * Object that represents each user
     */
    public User(Date sDateRegistered, String sEmail, String sFirstName, String sUserID) {
        dateRegistered = sDateRegistered;
        email = sEmail;
        firstName = sFirstName;
        userID = sUserID;
    }

    public User(Date sDateRegistered, String sFirstName, long sAge, String sBio, String pImage) {
        dateRegistered = sDateRegistered;
        firstName = sFirstName;
        age = sAge;
        bio = sBio;
        profileImage = pImage;
    }

    public Date getDateRegistered() {
        return dateRegistered;
    }

    public String getEmail() {
        return userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getUserID() {
        return userID;
    }

    public String getBio() {
        return bio;
    }

    public long getAge() {
        return age;
    }

    public Bitmap getProfileImage() {
        //Converts the imageB64 String back into Bitmap
        byte[] decodedString = Base64.decode(profileImage, Base64.URL_SAFE);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
}
