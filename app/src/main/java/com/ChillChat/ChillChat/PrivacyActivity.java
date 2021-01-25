package com.ChillChat.ChillChat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import static android.graphics.Color.parseColor;

public class PrivacyActivity extends AppCompatActivity {
    /**
     * Runs when onCreate() state is called.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Basic operations
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        getWindow().setStatusBarColor(parseColor("#3f77bc"));
    }

    /**
     * Event listener for the ImageButton on screen.
     * Takes the user back to the chat activity.
     */
    public void goBack(View view) {
        finish();
    }
}