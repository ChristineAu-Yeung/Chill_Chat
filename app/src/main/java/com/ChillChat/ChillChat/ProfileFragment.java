package com.ChillChat.ChillChat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    //PLEASE REFER TO ChatFragment TO SEE HOW THINGS NEED TO BE ALTERED FOR A FRAGMENT TYPE ACTIVITY!!! - Ryan

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        return root;
    }


}