package com.ChillChat.ChillChat;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import static android.graphics.Color.parseColor;

public class ChatActivity extends AppCompatActivity {
    //Variable for SharedPreference
    protected static final String FILE_NAME = "CurrentUser";

    /**
     Runs when onCreate() state is called.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Basic operations
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setStatusBarColor(parseColor("#0080ff"));
    }

    /**
     Runs when onStart() state is called.
     This function is used to check if the user is already signed in, preventing invalid login
     */
    @Override
    public void onStart() {
        super.onStart();
        //Open shared preference from file location and retrieve Email
        SharedPreferences prefs = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String username = prefs.getString("Email", "Void");
        //Compare the stored username to Void to see if a user is currently signed it
        if(username.compareTo("Void") == 0) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     Sets LoginActivity successful login variable to false 'logging user out'
     */
    public void Logout(View view) {
        //Open shared preference from file location and open editor
        SharedPreferences prefs = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        //Edit the DefaultEmail to be text from email and commit changes
        edit.putString("Email", "Void");
        edit.commit();
        //Set success to false then open activity
        LoginActivity.success = false;
        Intent intent = new Intent(ChatActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}