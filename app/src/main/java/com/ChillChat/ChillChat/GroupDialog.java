package com.ChillChat.ChillChat;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GroupDialog extends Dialog implements View.OnClickListener {
    public Activity c;
    GroupObject groupObj;
    Integer position;
    DatabaseService db;

    public GroupDialog(Activity a, GroupObject group, Integer pos) {
        super(a);
        this.c = a;
        groupObj = group;
        position = pos;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.group_dialog);

        //Open db connection
        db = new DatabaseService();

        //Show the password field if there is a password on group
        EditText password = findViewById(R.id.password);
        if (groupObj.getGroupPassword().isEmpty()) {
            password.setVisibility(View.GONE);
        }

        //Set the message
        TextView message = findViewById(R.id.message);
        message.setText("Would you like to change to " + groupObj.getGroupName() + "?");
        //Set click listener for copy button
        Button confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                //Get text from the view
                EditText password = findViewById(R.id.password);
                String result = password.getText().toString();

                //Check to see if the group has no password or entered password matches
                if (groupObj.getGroupPassword().isEmpty() || groupObj.getGroupPassword().equals(result)) {
                    //Change group
                    db.selectGroup(getContext(), position);
                    //Open the chat activity
                    Intent intent = new Intent(c, MenuActivity.class);
                    c.startActivity(intent);
                    //Dismiss the dialog
                    dismiss();
                } else {
                    //Toast
                    Toast toast = Toast.makeText(c, "Invalid password!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            default:
                break;
        }
    }
}
