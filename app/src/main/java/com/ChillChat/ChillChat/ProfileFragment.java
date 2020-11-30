package com.ChillChat.ChillChat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import androidx.annotation.NonNull;
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
    Uri imageUri;
    private final static int REQUEST_GALLERY = 10;

    //PLEASE REFER TO ChatFragment TO SEE HOW THINGS NEED TO BE ALTERED FOR A FRAGMENT TYPE ACTIVITY!!! - Ryan

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        editButton = (Button) root.findViewById(R.id.editButton);
        profileImageButton = (ImageButton) root.findViewById(R.id.profilePictureImageButton);
        name = (EditText) root.findViewById(R.id.nameEditText);
        age = (EditText) root.findViewById(R.id.ageEditText);
        bio = (EditText) root.findViewById(R.id.bioEditText);

        //pulls profile data
        GetProfile();

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "updated user collection");
                String ageString = age.getText().toString();
                long ageNum = Long.parseLong(ageString);
                SetProfile(name.getText().toString(), ageNum, bio.getText().toString());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        profileImageButton = (ImageButton) getView().findViewById(R.id.profilePictureImageButton);
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            try {
                Uri imageUri = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),imageUri);
                profileImageButton.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void SetProfile(String name, long age, String bio){ db.setProfileData(name, age, bio); }

    private void GetProfile(){
        db.getProfileData(db.getUID(), getActivity());
    }
}