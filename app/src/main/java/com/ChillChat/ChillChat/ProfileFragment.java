package com.ChillChat.ChillChat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.graphics.BitmapCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.data.BitmapTeleporter;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    protected DatabaseService db = new DatabaseService();
    private static final String TAG = "ProfileFragment";
    private Button editButton;
    private ImageButton profileImageButton;
    private EditText name;
    private EditText age;
    private EditText bio;
    private final static int REQUEST_GALLERY = 10;

    /**
     * Gets fired when the fragment is first created
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        editButton = (Button) root.findViewById(R.id.editButton);
        profileImageButton = (ImageButton) root.findViewById(R.id.profilePictureImageButton);
        name = (EditText) root.findViewById(R.id.nameEditText);
        age = (EditText) root.findViewById(R.id.ageEditText);
        bio = (EditText) root.findViewById(R.id.bioEditText);

        //Set the user's current timestamp
        DatabaseService db = new DatabaseService();
        String userID = db.getUID();
        db.setUserTimestamp(userID);

        //pulls profile data
        GetProfile();

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().equals("Anonymous")) {
                    Toast.makeText(getContext(), "Name can't be 'Anonymous'!", Toast.LENGTH_SHORT).show();
                } else {
                    //Sets the profileImage Bitmap to allow for profile updates without image swap.
                    Bitmap pImage = null;
                    BitmapDrawable drawable = (BitmapDrawable) profileImageButton.getDrawable();
                    pImage = drawable.getBitmap();
                    //Continue normal process
                    Log.i(TAG, "updated user collection");
                    String ageString = age.getText().toString();
                    long ageNum = Long.parseLong(ageString);
                    String imageB64 = getImageData(pImage);
                    if (imageB64.length() * 2 < 1200000) {
                        SetProfile(name.getText().toString(), ageNum, bio.getText().toString(), imageB64);
                        Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "The file you selected is too large!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Image Button to Open Gallery to choose Profile Picture
        profileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, REQUEST_GALLERY);
            }
        });
        return root;
    }

    /**
     * Gets fired after the user has selected an image
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        profileImageButton = (ImageButton) getView().findViewById(R.id.profilePictureImageButton);
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            Bitmap profileImage = null;
            try {
                Uri imageUri = data.getData();
                profileImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),imageUri);
                profileImageButton.setImageBitmap(profileImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This function sets the profile data from the cloud firestore.
     */
    private void SetProfile(String name, long age, String bio, String pImage){
        db.setProfileData(name, age, bio, pImage);
    }

    /**
     * This function gets the profile data from the cloud firestore.
     * The function has to set the view data inside to prevent async issues
     */
    private void GetProfile(){
        db.getProfileData(db.getUID(), getActivity());
    }

    /**
     * This function takes in a Bitmap image and returns a string of Byte64 to be stored in the database.
     */
    public String getImageData(Bitmap bmp) {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, bao); // bmp is bitmap from user image file
        byte[] byteArray = bao.toByteArray();
        String imageB64 = Base64.encodeToString(byteArray, Base64.URL_SAFE);
        return imageB64;
    }
}