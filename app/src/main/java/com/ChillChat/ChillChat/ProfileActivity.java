package com.ChillChat.ChillChat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import static android.graphics.Color.parseColor;

public class ProfileActivity extends AppCompatActivity {

    protected DatabaseService db = new DatabaseService();
    private Button editButton;

    /**
     * Runs when onCreate() state is called.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Basic operations
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().setStatusBarColor(parseColor("#3f77bc"));

        findViewById(R.id.profilePictureImageButton);
        findViewById(R.id.nameEditText);
        findViewById(R.id.ageEditText);
        findViewById(R.id.bioEditText);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        if(!userID.equals("")) {
            GetProfile(userID);
        } else {
            GetProfile("");
        }
    }

    /**
     * Event listener for the ImageButton on screen.
     * Takes the user back to the chat activity.
     */
    public void goBack(View view) {
        finish();
    }

    /**
     * This function gets the profile data from the cloud firestore.
     * The function has to set the view data inside to prevent async issues
     */
    private void GetProfile(String userID){
        //Need to use the ID that is passed in from bundle
        db.getProfileData(userID, ProfileActivity.this);
    }

}