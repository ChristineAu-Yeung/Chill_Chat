package com.ChillChat.ChillChat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    protected DatabaseService db = new DatabaseService();
    public static ArrayList<String> userData;
    private static final String TAG = "ProfileFragment";
    private Button editButton;
    private EditText name;
    private EditText age;
    private EditText bio;
    private TextView joinDate;

    //PLEASE REFER TO ChatFragment TO SEE HOW THINGS NEED TO BE ALTERED FOR A FRAGMENT TYPE ACTIVITY!!! - Ryan

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        userData = new ArrayList<>();
        editButton = (Button) root.findViewById(R.id.editButton);
        name = (EditText) root.findViewById(R.id.nameEditText);
        age = (EditText) root.findViewById(R.id.ageLabelEditText);
        bio = (EditText) root.findViewById(R.id.bioEditText);
        joinDate = (TextView) root.findViewById(R.id.registeredLabelTextView);

        PullProfile();

//        Log.i(TAG, userData.get(0));

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "updated user collection");
                SetProfile("brian@test.com", name.getText().toString());
            }
        });

        return root;
    }

    private void SetProfile(String email, String name){
        db.updateUserData(email, name);
    }

    private void PullProfile(){

        db.getUserData();

//        Log.i(TAG, userData.get(0));
    }
}