package com.ChillChat.ChillChat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static android.graphics.Color.parseColor;
import static com.ChillChat.ChillChat.DatabaseService.deleteAnonymousUser;

public class ProfileActivity extends AppCompatActivity {

    protected DatabaseService db = new DatabaseService();
    private Button editButton;
    private String userID;

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
        userID = intent.getStringExtra("userID");
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
     * Event listener for the report Button on screen.
     * Asks the user if they want to report the user then adds reported user to table
     * for manual review and potential ban
     */
    public void reportUser(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setMessage("Would you like to report this user?")
                .setTitle("Attention")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Open shared preference from file location and open editor
                        //Need to add userID to reportedUser table
                        Toast.makeText(ProfileActivity.this, "User reported!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Do nothing
                    }
                })
                .show();
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