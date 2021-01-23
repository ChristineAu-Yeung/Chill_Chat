package com.ChillChat.ChillChat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ChatFragment extends Fragment {
    //Variable for SharedPreference
    protected static final String FILE_NAME = "CurrentUser";
    private static final String TAG = "ChatFragment";

    ListView chatListView;
    EditText chatEditText;
    Button sendButton;

    static ChatAdapter messageAdapter;
    public static ArrayList<ChatMessage> chatMessages;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        final DatabaseService db = new DatabaseService();

        //Checks for the messages and adds them to chatMessages
        checkChat(getContext(), db);

        //Start notification service to check for messages in background
        Intent intent = new Intent(getContext(), NotificationService.class);
        getContext().startService(intent);

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
                if (text.trim().length() > 0 && text.trim().length() == 0) {
                    //Toast toast = Toast.makeText(ChatFragment.this, "Empty text try again", Toast.LENGTH_SHORT);
                    //toast.show();

                    chatEditText.setText("");
                } else if (text.length() > 0) {

                    ChatMessage message = new ChatMessage(
                            text,
                            DatabaseService.getDisplayName(),
                            DatabaseService.getGroupNumber(getContext()),
                            null, // NULL because we want to generate a new ID
                            DatabaseService.getUID());
                    chatMessages.add(message);
                    db.sendMessageHelper(message);

                    messageAdapter.notifyDataSetChanged();

                    chatEditText.setText("");
                }
            }
        });

        chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatMessage chatObject = chatMessages.get(position);
                if (!chatObject.userID.equals(DatabaseService.getUID())) {
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    intent.putExtra("userID", chatObject.userID);
                    startActivity(intent);
                }
            }
        });

        return root;
    }

    public static void checkChat(Context ctx, DatabaseService db) {
        db.getMessageHelper(ctx);
    }

    /**
     * Runs when onStart() state is called.
     * This function is used to check if the user is already signed in, preventing invalid login
     */
    @Override
    public void onStart() {
        super.onStart();
        //Open shared preference from file location and retrieve Email
        SharedPreferences prefs = getActivity().getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String username = prefs.getString("Email", "Void");
        //Compare the stored username to Void to see if a user is currently signed it
        if (username.compareTo("Void") == 0) {
            Intent intent = new Intent(this.getActivity(), LoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Helper function that lets DatabaseService notify messageAdapter that the message list
     * was updated
     */
    public static void externallyCallDatasetChanged() {
        messageAdapter.notifyDataSetChanged();
        Log.i(TAG, "Externally called notifyDataSetChanged()");
    }

    private class ChatAdapter extends ArrayAdapter<String> {
        public ChatAdapter(Context context) {
            super(context, 0);
        }

        public int getCount() {
            return chatMessages.size();
        }

        public String getItem(int position) {
            return chatMessages.get(position).message;
        }

        //Returns the message from chat at provided position
        public ChatMessage getChatMessage(int position) {
            return chatMessages.get(position);
        }

        //Gets run for each message in the Array
        @SuppressLint("InflateParams")
        public View getView(int position, View convertView, ViewGroup parent) {
            //Create inflater and set to current view
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View result = null;

            //Open new DatabaseService and get the user ID
            DatabaseService db = new DatabaseService();
            String currentUser = DatabaseService.getUID();
            //Get the ChatMessage at provided position
            ChatMessage chatObject = getChatMessage(position);
            ImageView userPic;

            //If the chat userID is equal to the ID of the current user, inflate with outgoing view
            if (currentUser.equals(chatObject.userID)) {
                result = inflater.inflate(R.layout.chat_row_outgoing, null);
                userPic = result.findViewById(R.id.outUser);
            } else { //Else, inflate the incoming view. Set userPic ImageView to correct id
                result = inflater.inflate(R.layout.chat_row_incoming, null);
                userPic = result.findViewById(R.id.incUser);
            }

            //Get the user data from database
            DatabaseService.getUserData(chatObject.userID, result, userPic);
            TextView message = result.findViewById(R.id.message_text);
            message.setText(getItem(position));  // get str at position

            return result;
        }
    }
}