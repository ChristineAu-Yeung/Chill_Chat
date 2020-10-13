package com.ChillChat.ChillChat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import static android.graphics.Color.parseColor;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Basic operations
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setStatusBarColor(parseColor("#0080ff"));
    }

    /**
     Sets LoginActivity successful login variable to false 'logging user out'
     */
    public void Logout(View view) {
        LoginActivity.success = false;
        Intent intent = new Intent(ChatActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}