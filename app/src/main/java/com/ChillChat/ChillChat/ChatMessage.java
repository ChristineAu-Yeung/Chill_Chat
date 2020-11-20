package com.ChillChat.ChillChat;

public class ChatMessage {

    String message;
    String firstName;
    int groupNumber;
//        String imageUrl; // For the future

    public ChatMessage(String messageToBeSent, String userFirstName, int groupNum) {
        message = messageToBeSent;
        firstName = userFirstName;
        groupNumber = groupNum;
    }

}
