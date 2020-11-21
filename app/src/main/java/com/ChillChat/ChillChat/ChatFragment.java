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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ChatFragment extends Fragment {
    //Variable for SharedPreference
    protected static final String FILE_NAME = "CurrentUser";

    ListView chatListView;
    EditText chatEditText;
    Button sendButton;

    ChatAdapter messageAdapter;
    ArrayList<String> chatMessages;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        final DatabaseService db = new DatabaseService();

        // Gets all the messages and keeps getting em
        db.getMessages(0);

        chatListView = root.findViewById(R.id.chatListView);
        chatEditText = root.findViewById(R.id.chatEditText);
        sendButton = root.findViewById(R.id.sendButton);

        chatMessages = new ArrayList<>();
        messageAdapter = new ChatAdapter(this.getActivity());
        chatListView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get text from edit text
                String text = chatEditText.getText().toString();

                // If len > 0, add to chatMessages and notify the message adapter.
                // Empty the EditText
                if(text.trim().length() > 0 && text.trim().length() == 0){

                    //Shits not working
                    //Toast toast = Toast.makeText(ChatFragment.this, "Empty text try again", Toast.LENGTH_SHORT);
                    //toast.show();

                    chatEditText.setText("");
                }
                else if(text.length() > 0){
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

        return root;
    }

    /**
     Runs when onStart() state is called.
     This function is used to check if the user is already signed in, preventing invalid login
     */
    @Override
    public void onStart() {
        super.onStart();
        //Open shared preference from file location and retrieve Email
        SharedPreferences prefs = getActivity().getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String username = prefs.getString("Email", "Void");
        //Compare the stored username to Void to see if a user is currently signed it
        if(username.compareTo("Void") == 0) {
            Intent intent = new Intent(this.getActivity(), LoginActivity.class);
            startActivity(intent);
        }
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
            LayoutInflater inflater = getActivity().getLayoutInflater();

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