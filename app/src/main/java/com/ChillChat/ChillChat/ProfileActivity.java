package com.ChillChat.ChillChat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;
import static android.graphics.Color.parseColor;

public class ProfileActivity extends AppCompatActivity {

    protected DatabaseService db = new DatabaseService();
    private Button editButton;
    private ImageView profileImageButton;
    private EditText name;
    private EditText age;
    private EditText bio;

    /**
     * Runs when onCreate() state is called.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Basic operations
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().setStatusBarColor(parseColor("#3f77bc"));

        profileImageButton = (ImageView) findViewById(R.id.profilePictureImageButton);
        name = (EditText) findViewById(R.id.nameEditText);
        age = (EditText) findViewById(R.id.ageEditText);
        bio = (EditText) findViewById(R.id.bioEditText);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        if(!userID.equals("")) {
            GetProfile(userID);
        } else {
            GetProfile("");
        }
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