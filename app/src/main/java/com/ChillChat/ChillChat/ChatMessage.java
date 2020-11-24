package com.ChillChat.ChillChat;

public class ChatMessage {

    String message;
    String firstName;
    int groupNumber;
    String messageID;
//        String imageUrl; // For the future

    public ChatMessage(String messageToBeSent, String userFirstName, int groupNum) {
        message = messageToBeSent;
        firstName = userFirstName;
        groupNumber = groupNum;

        messageID = this.generateID(messageToBeSent, userFirstName);
    }

    private String generateID(String message, String userFirstName){

//        message.
        return message + userFirstName;
    }
}
