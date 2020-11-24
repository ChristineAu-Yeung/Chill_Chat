package com.ChillChat.ChillChat;

import java.util.Calendar;

public class ChatMessage {

    String message;
    String firstName;
    int groupNumber;
    int messageID;
//        String imageUrl; // For the future

    public ChatMessage(String messageToBeSent, String userFirstName, int groupNum) {
        message = messageToBeSent;
        firstName = userFirstName;
        groupNumber = groupNum;

        messageID = this.generateID();
    }

    private int generateID() {

        String currentTime = Calendar.getInstance().getTime().toString();

        return (this.firstName + this.message + currentTime).hashCode();
    }
}
