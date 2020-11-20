package com.ChillChat.ChillChat;

import androidx.appcompat.app.AppCompatActivity;

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

import static android.graphics.Color.parseColor;

public class ChatActivity extends AppCompatActivity {
    //Variable for SharedPreference
    protected static final String FILE_NAME = "CurrentUser";

    ListView chatListView;
    EditText chatEditText;
    Button logoutButton;
    Button sendButton;
    ChatAdapter messageAdapter;

    ArrayList<String> chatMessages;

    /**
     Runs when onCreate() state is called.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Basic operations
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setStatusBarColor(parseColor("#0080ff"));

        final DatabaseService db = new DatabaseService();

        chatListView = findViewById(R.id.chatListView);
        chatEditText = findViewById(R.id.chatEditText);
        logoutButton = findViewById(R.id.logoutButton);
        sendButton = findViewById(R.id.sendButton);

        chatMessages = new ArrayList<>();

        messageAdapter = new ChatAdapter(this);
        chatListView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get text from edit text
                String text = chatEditText.getText().toString();

                // If len > 0, add to chatMessages and notify the message adapter.
                // Empty the EditText
                if(text.length() != 0){
                    // TESTING
                    ChatMessage message = new ChatMessage(text, DatabaseService.getDisplayName(), 0);
                    chatMessages.add(text);
                    db.sendMessage(message);
                    // ---------------------------------------------


//                    chatMessages.add(text);
                    messageAdapter.notifyDataSetChanged();
                    chatEditText.setText("");
                }
            }
        });

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

    private class ChatAdapter extends ArrayAdapter<String> {
        public ChatAdapter(Context context){
            super(context, 0);
        }

        public int getCount(){
            return chatMessages.size();
        }

        public String getItem(int position){
            return chatMessages.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = ChatActivity.this.getLayoutInflater();

            View result = null;

//            if (position % 2 == 0){
//                result = inflater.inflate(R.layout.chat_row_incoming, null);
//            } else {
//                result = inflater.inflate(R.layout.chat_row_outgoing, null);
//            }

            result = inflater.inflate(R.layout.chat_row_outgoing, null);

            TextView message = result.findViewById(R.id.message_text);
            message.setText(getItem(position));  // get str at position

            return result;
        }
    }


}