package com.ChillChat.ChillChat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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

import com.google.firebase.firestore.FieldValue;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class GroupListFragment extends Fragment {
    //Variable for SharedPreference
    private static final String TAG = "GroupListFragment";

    private static final String defaultImage = "https://static.thenounproject.com/png/3246632-200.png";
    private static final String userImage = "https://i.pinimg.com/originals/0c/3b/3a/0c3b3adb1a7530892e55ef36d3be6cb8.png";

    ListView groupListView;

    static GroupAdapter groupAdapter;
    public static ArrayList<User> userList;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_grouplist, container, false);

        final DatabaseService db = new DatabaseService();

        //Set the user's current timestamp
        String userID = db.getUID();
        db.setUserTimestamp(userID);

        //Get the group members from DB
        db.getGroupList(getContext());

        groupListView = root.findViewById(R.id.groupListView);

        userList = new ArrayList<>();
        groupAdapter = new GroupAdapter(this.getActivity());
        groupListView.setAdapter(groupAdapter);

        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User userObject = userList.get(position);
                if (!userObject.getUserID().equals(DatabaseService.getUID())) {
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    intent.putExtra("userID", userObject.getUserID());
                    startActivity(intent);
                }
            }
        });

        return root;
    }

    /**
     * Helper function that lets DatabaseService notify messageAdapter that the message list
     * was updated
     */
    public static void externallyCallDatasetChanged() {
        groupAdapter.notifyDataSetChanged();
        Log.i(TAG, "Externally called notifyDataSetChanged()");
    }

    private class GroupAdapter extends ArrayAdapter<String> {
        public GroupAdapter(Context context) {
            super(context, 0);
        }

        public int getCount() {
            return userList.size();
        }

        public String getItem(int position) {
            return userList.get(position).getFirstName();
        }

        //Returns the user from list at provided position
        public User getUser(int position) {
            return userList.get(position);
        }

        //Gets run for each message in the Array
        @SuppressLint("InflateParams")
        public View getView(int position, View convertView, ViewGroup parent) {
            //Create inflater and set to current view
            LayoutInflater inflater = getActivity().getLayoutInflater();

            //Get the user at provided position
            User userObject = getUser(position);

            //If the chat userID is equal to the ID of the current user, inflate with outgoing view
            View result = inflater.inflate(R.layout.grouplist_row_user, null);

            //Code to set the user picture if they have one in the group list
            ImageView userPic = result.findViewById(R.id.userImg);
            if ("Anonymous".equals(userObject.getFirstName())) {
                Picasso.get().load(defaultImage).into(userPic);
            } else {
                Bitmap bmpImage = userObject.getProfileImage();
                if (bmpImage != null) {
                    userPic.setImageBitmap(bmpImage);
                } else {
                    Picasso.get().load(userImage).into(userPic);
                }
            }

            //Sets the userList element to online/offline based on if user has been in chat within last 3 minutes
            ImageView statusImg = result.findViewById(R.id.statusImage);
            TextView status = result.findViewById(R.id.status);
            //Get the time now and latest time of user action recorded
            Date now = new Date();
            Date latestTime = userObject.getLatestTime();
            if (userObject.getUserID().equals(DatabaseService.getUID()) || (latestTime != null && now.getTime() - latestTime.getTime() < 3*60*1000)) {
                //Set online status
                statusImg.setImageResource(R.drawable.ic_online);
                status.setText("Online");
            }

            //This sets the user's first name
            TextView userName = result.findViewById(R.id.user_name);
            userName.setText(getItem(position));  // get str at position

            return result;
        }
    }
}