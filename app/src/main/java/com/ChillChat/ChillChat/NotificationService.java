package com.ChillChat.ChillChat;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationService extends Service {
    //Global variables
    String TAG = "NotificationService";
    FirebaseFirestore db;

    /**
     * Called when the service is first created
     */
    @Override
    public void onCreate() {
        Log.d("NotificationService", "Service starting");
        // Create Instance of Firestore
        db = FirebaseFirestore.getInstance();
        // Start message checking process
        createNotificationChannel();
        startProcess();
    }

    /**
     * Called when the service is first started, due to a call to startService()
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("NotificationService", "Service called by user");

        //START_STICKY will recreate the service and call onStartCommand()
        return START_STICKY;
    }

    /**
     * We don't provide binding, so return null
     */
    @Override
    public IBinder onBind(Intent intent) { return null; }

    /**
     * The service is no longer used and is being destroyed
     */
    @Override
    public void onDestroy() {
        Log.d("NotificationService", "Service done");
    }

    /**
     * Creates a notification channel where the notifications will live
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ChillChat";
            String description = "Chill Chat Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("ChillChat", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * This function gets the group number from shared preferences and then the documentID associated
     * from the DB. It then calls startValueListener.
     */
    public void startProcess() {
        //Get collection reference of groups
        final CollectionReference groupCollection = db.collection("groups");

        //First get the group # from the shared preferences
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("CurrentUser", MODE_PRIVATE);
        final Integer groupNumber = prefs.getInt("groupNumber", 0);
        final ArrayList<String> documentID = new ArrayList<>();

        //Get the groupDocumentString from database based on group # int
        groupCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        documentID.add(document.getId());
                    }
                    //Start value listener to check for messages
                    startValueListener(documentID.get(groupNumber), groupNumber);
                } else {
                    Log.i(TAG, "Unsuccessful");
                }
            }
        });
    }

    /**
     * This function gets the group number from shared preferences and then the documentID associated
     * from the DB. It then calls startValueListener.
     */
    public void startValueListener(String groupDocumentString, final int groupNumber) {
        //Get collection reference of groups
        final CollectionReference groupCollection = db.collection("groups");
        //Get new messages from database based on group groupDocumentString
        groupCollection.document(groupDocumentString)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            // Ignore the warning here
                            ArrayList<HashMap<String, String>> incomingMessages = (ArrayList<HashMap<String, String>>) snapshot.getData().get("messages");
                            if (incomingMessages != null) {
                                //Get the last sent message from incomingMessages
                                Integer i = incomingMessages.size()-1;
                                ChatMessage incomingMessage = new ChatMessage(
                                        incomingMessages.get(i).get("message"),
                                        incomingMessages.get(i).get("sender"),
                                        groupNumber,
                                        incomingMessages.get(i).get("msgId"),
                                        incomingMessages.get(i).get("userID"));
                                //Create notification based on last sent message
                                createNotification(incomingMessage.firstName, incomingMessage.message);
                                Log.d(TAG, "New Message Query Complete");
                            } else {
                                Log.d(TAG, "Skipped new message query. Existing data is up to date.");
                            }

                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });
    }

    /**
     * This function creates a new notification using the first name and message sent by the user
     */
    public void createNotification(String fName, String message) {
        Context context = getApplicationContext();
        //Get the currently open app
        ActivityManager am = (ActivityManager) context
                .getSystemService(Activity.ACTIVITY_SERVICE);
        String packageName = am.getRunningTasks(1).get(0).topActivity
                .getPackageName();

        //If the currently open app is not ChillChat send notification about new message
        if (!packageName.equals("com.ChillChat.ChillChat")) {
            // Create an explicit intent for an Activity in your app
            Intent intent = new Intent(context, MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            //Strip the message to 70 characters max
            String nMessage = message;
            nMessage = nMessage.substring(0, Math.min(nMessage.length(), 70));

            //Build the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ChillChat")
                    .setSmallIcon(R.drawable.ic_logo_noti)
                    .setContentTitle(fName)
                    .setContentText(nMessage)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            //Show the notification
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            // If I want to have multiple notifications need to change the notification ID to be unique each time
            notificationManager.notify(0, builder.build());
        }
    }
}
