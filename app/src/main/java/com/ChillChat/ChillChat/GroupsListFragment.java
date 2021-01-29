package com.ChillChat.ChillChat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class GroupsListFragment extends Fragment {
    //Variable for SharedPreference
    private static final String TAG = "GroupsListFragment";

    ListView groupsListView;
    static GroupsAdapter groupsAdapter;
    public static ArrayList<String> groupsList;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_groupslist, container, false);

        final DatabaseService db = new DatabaseService();
        //Set the user's current timestamp
        String userID = db.getUID();
        db.setUserTimestamp(userID);
        //Get the groups list from DB
        db.getGroupsList();

        groupsListView = root.findViewById(R.id.groupsListView);
        groupsList = new ArrayList<>();
        groupsAdapter = new GroupsAdapter(this.getActivity());
        groupsListView.setAdapter(groupsAdapter);

        //On Item click listener for selected group
        groupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                SharedPreferences prefs = getContext().getSharedPreferences("CurrentUser", MODE_PRIVATE);
                Integer groupNum = prefs.getInt("groupNumber", 0);
                if (position != groupNum) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Would you like to change to Group " + (position+1) + "?")
                        .setTitle("Group Change")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                //Call that lets the user select the group that they are in
                                DatabaseService db = new DatabaseService();
                                db.selectGroup(getContext(), position);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .show();
                }
            }
        });

        //On click listener for random group button
        Button randGroup = root.findViewById(R.id.randGroup);
        randGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure you want to change groups?")
                        .setTitle("Group Change")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                //Call the function to place user in a new randomized group
                                DatabaseService db = new DatabaseService();
                                db.randomizeGroup(getContext());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .show();
                Log.i("MenuActivity", "User tapped the rng button");
            }
        });

        //On click listener for random group button
        //Button newGroup = root.findViewById(R.id.newGroup);

        return root;
    }

    /**
     * Helper function that lets DatabaseService notify messageAdapter that the message list
     * was updated
     */
    public static void externallyCallDatasetChanged() {
        groupsAdapter.notifyDataSetChanged();
        Log.i(TAG, "Externally called notifyDataSetChanged()");
    }

    private class GroupsAdapter extends ArrayAdapter<String> {
        public GroupsAdapter(Context context) {
            super(context, 0);
        }

        public int getCount() {
            return groupsList.size();
        }

        public String getItem(int position) {
            return groupsList.get(position);
        }

        //Gets run for each message in the Array
        @SuppressLint("InflateParams")
        public View getView(int position, View convertView, ViewGroup parent) {
            //Create inflater and set to current view
            LayoutInflater inflater = getActivity().getLayoutInflater();

            //If the chat userID is equal to the ID of the current user, inflate with outgoing view
            View result = inflater.inflate(R.layout.grouplist_row_group, null);

            //Get the current group of the user
            ImageView currentGroup = result.findViewById(R.id.currentGroup);
            SharedPreferences prefs = getContext().getSharedPreferences("CurrentUser", MODE_PRIVATE);
            Integer groupNum = prefs.getInt("groupNumber", 0);
            if (position != groupNum) {
                currentGroup.setVisibility(View.GONE);
            }

            //This sets the group number at position + 1
            TextView groupNumber = result.findViewById(R.id.groupNumber);
            groupNumber.setText("Group " + (position + 1));

            //Get the groupID at provided position
            String groupID = getItem(position);
            //Get the group member and message counts from DB
            DatabaseService db = new DatabaseService();
            db.getGroupCounts(groupID, result);

            return result;
        }
    }
}