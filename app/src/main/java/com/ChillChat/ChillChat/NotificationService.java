package com.ChillChat.ChillChat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class NotificationService extends Service {
    DatabaseService db;

    @Override
    public void onCreate() {
        //Create new DatabaseService
        db = new DatabaseService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("NotificationService", "Service starting");
        // The service is starting, due to a call to startService()
        // Gets all the messages and keeps getting em
        db.getMessageHelper(getApplicationContext());

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        Log.d("NotificationService", "Service done");
    }
}
