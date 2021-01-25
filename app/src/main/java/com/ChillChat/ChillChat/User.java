package com.ChillChat.ChillChat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.util.Date;

public class User {
    private Date dateRegistered;
    private String firstName;
    private long age;
    private String bio;
    private String profileImage;
    private Date latestTime;
    private String userID;

    /**
     * Object that represents each user
     */
    public User(Date sDateRegistered, String sFirstName, long sAge, String sBio, String pImage) {
        dateRegistered = sDateRegistered;
        firstName = sFirstName;
        age = sAge;
        bio = sBio;
        profileImage = pImage;
    }

    public User(Date sDateRegistered, String sFirstName, long sAge, String sBio, String pImage, Date lTime, String uID) {
        dateRegistered = sDateRegistered;
        firstName = sFirstName;
        age = sAge;
        bio = sBio;
        profileImage = pImage;
        latestTime = lTime;
        userID = uID;
    }

    public Date getDateRegistered() {
        return dateRegistered;
    }

    public String getFirstName() {
        return firstName;
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

    public Date getLatestTime() {
        return latestTime;
    }

    public String getUserID() {
        return userID;
    }
}
