package com.ChillChat.ChillChat;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ChatMessage {

    String message;
    String firstName;
    String messageID;
    String userID;
    Date messageSent;
    int groupNumber;

//        String imageUrl; // For the future

    /**
     * Object that represents each message being send/received
     *
     * @param messageToBeSent - [STRING] Message being sent (or received
     * @param userFirstName   - [STRING] First name of the person who sent the message
     * @param groupNum        - [INT] Group number, to see which group the message belongs in
     * @param msgID           - [STRING] Null if the message is new. Not null if message is being
     *                        imported from the database. Unique ID # for the message so there are no dupes
     * @param UID             - [STRING] Sender's UID
     */
    public ChatMessage(String messageToBeSent, String userFirstName, int groupNum, String msgID, String UID) {
        message = messageToBeSent;
        firstName = userFirstName;
        groupNumber = groupNum;
        userID = UID;

        // If the message is null, we generate a new ID.
        // This is so that when we get message data from the database, it doesn't generate a new ID.
        if (msgID == null) {
            messageID = this.generateID();
        } else {
            messageID = msgID;
        }

    }

    /**
     * Same as above but with extra mSent date parameter
     */
    public ChatMessage(String messageToBeSent, String userFirstName, int groupNum, String msgID, String UID, Date mSent) {
        message = messageToBeSent;
        firstName = userFirstName;
        groupNumber = groupNum;
        userID = UID;
        messageSent = mSent;

        // If the message is null, we generate a new ID.
        // This is so that when we get message data from the database, it doesn't generate a new ID.
        if (msgID == null) {
            messageID = this.generateID();
        } else {
            messageID = msgID;
        }

    }

    private String generateID() {

        String currentTime = Calendar.getInstance().getTime().toString();

        return String.valueOf((this.firstName + this.message + currentTime).hashCode());
    }

    /**
     * Had to Override this so we can compare messages by messageID and ONLY messageID.
     *
     * @param o - Object that the message is being compared to
     * @return - True if equal. False if not.
     */
    @Override
    public boolean equals(Object o) {
        // Checks if the object instance is exactly the same
        if (this == o) return true;

        // Checks if the class type is the same
        if (o == null || getClass() != o.getClass()) return false;

        // References the other object, o
        ChatMessage that = (ChatMessage) o;

        // Compares the ID's
        return messageID.equals(that.messageID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, firstName, groupNumber, messageID);
    }
}
