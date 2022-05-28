package com.ChillChat.ChillChat;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageDialog extends Dialog implements android.view.View.OnClickListener {
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
        //Set click listener for copy button
        Button copy = findViewById(R.id.copy);
        copy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.copy:
                //Get text from the view
                TextView message = findViewById(R.id.message);
                String result = message.getText().toString();
                //Copy the message to the clipboard
                ClipboardManager clipboard = (ClipboardManager) c.getSystemService(c.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Message", result);
                clipboard.setPrimaryClip(clip);
                break;
            default:
                break;
        }
        //Dismiss the dialog
        dismiss();
    }
}
