package com.ChillChat.ChillChat;

public class GroupObject {
    private String groupID;
    private String groupName;
    private String groupPassword;

    /**
     * Object that represents each group
     */
    public GroupObject(String gID, String gName, String gPassword) {
        groupID = gID;
        groupName = gName;
        groupPassword = gPassword;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupPassword() {
        //Will need to do decryption here once at that step
        return groupPassword;
    }

}
