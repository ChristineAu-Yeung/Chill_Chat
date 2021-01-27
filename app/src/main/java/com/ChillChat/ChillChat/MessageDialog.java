package com.ChillChat.ChillChat;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageDialog extends Dialog {
    public Activity c;
    ChatMessage cMessage;

    public MessageDialog(Activity a, ChatMessage cMess) {
        super(a);
        this.c = a;
        cMessage = cMess;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.message_dialog);

        //Set the background to be blue or grey based on sent message
        RelativeLayout rLay = findViewById(R.id.relLayout);
        DatabaseService db = new DatabaseService();
        if (cMessage.userID.equals(db.getUID())) {
            rLay.setBackgroundResource(R.drawable.dialog_rectangle_blue);
        } else {
            rLay.setBackgroundResource(R.drawable.dialog_rectangle_grey);
        }

        //Set users first name
        TextView name = findViewById(R.id.name);
        name.setText(cMessage.firstName);
        //Set message sent date
        TextView mSent = findViewById(R.id.messageSent);
        Date messageSent = cMessage.messageSent;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
        mSent.setText(sdf.format(messageSent));
        //Set users message
        TextView message = findViewById(R.id.message);
        message.setText(cMessage.message);
    }

}
