package com.example.tholok.lab04;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static java.lang.Thread.sleep;

public class MessageFetcherService extends Service {

    FirebaseDatabase database;
    DatabaseReference myRef;

    private String TAG = "MessageFetcherService";

    public MessageFetcherService() {

        Log.e(TAG, "service started");

        // setup database// Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("messages");

        // start off dispatcher service
        DispatcherTask dispatcherTask = new  DispatcherTask();

          /*
        In modern API levels only one AsyncTask can be running at once, and this service has an
        AsyncTask running forever. This task is blocking all other tasks. A (and dirty) fix to this
        is what I've done here: just make AsyncTask execute in parallel.
        In the future I'll be avoiding having long-lived AsyncTasks
        If you're in IMT3673; see issue #20
         */
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
            dispatcherTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            dispatcherTask.execute();
        }
        
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class DispatcherTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            // TBD

            // sleep prefs amount of time then fetch topics, update db and notify MainActivity
            while (true) {

                // fetch pref sleep
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MessageFetcherService.this);
                int minutes = prefs.getInt("fetch-rate", 10);

                // convert to milliseconds
                int milliseconds = minutes*60*1000;

                Log.e(TAG, "Pref milli: " + Integer.toString(milliseconds));

                // try and sleep
                try {
                    sleep(/*milliseconds*/milliseconds);
                } catch (Exception ex) {
                    Log.e(TAG, "Couldn't sleep.. : " + ex.getMessage());
                }

                // do work
                Log.e(TAG, "DOING WORK");

                // if app isn't running -> check for messages
                if (!MainActivity.isVisible) {
                    checkForNewMessages();
                }

            }
        }
    }

    /**
     * Check for new messages. If there are any -> make notification about it that sends user to
     * messages screen
     */
    private void checkForNewMessages() {

        Log.e(TAG, "checking for new messages...");

        myRef.orderByChild("d").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    Log.e(TAG, "Querying message -> no datasnapshot exists");
                    return;
                }

                Message message = dataSnapshot.getChildren().iterator().next().getValue(Message.class);
                Log.e(TAG, "Message retrieved:::: " + message.m);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MessageFetcherService.this);

                long lastTimestamp = prefs.getLong("latest-message-timestamp", 0);
                long timestamp = Long.parseLong(message.d);

                // if is there are new messages and this isn't the first time the thing runs -> send notification
                if (timestamp > lastTimestamp && lastTimestamp != 0) {

                    Intent intent = new Intent(MessageFetcherService.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(MessageFetcherService.this, 0, intent, 0);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(MessageFetcherService.this)
                                    .setContentIntent(pendingIntent)
                                    .setSmallIcon(R.drawable.ic_launcher_background)
                                    .setContentTitle("New message(s) in the chatting app!")
                                    .setContentText("There are new message(s) in the chatting app. Click here to go there and check them out");


                    // Gets an instance of the NotificationManager service//

                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    // When you issue multiple notifications about the same type of event,
                    // it’s best practice for your app to try to update an existing notification
                    // with this new information, rather than immediately creating a new notification.
                    // If you want to update this notification at a later date, you need to assign it an ID.
                    // You can then use this ID whenever you issue a subsequent notification.
                    // If the previous notification is still visible, the system will update this existing notification,
                    // rather than create a new one. In this example, the notification’s ID is 001//

                    mNotificationManager.notify(9239239, mBuilder.build());

                    Log.e(TAG, "notifying");

                }



                // store date of most recent message
                SharedPreferences.Editor editor = prefs.edit();

                editor.putLong("latest-message-timestamp", timestamp);
                editor.apply();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Querying for recent messages failed..");
            }

        });
    }
}
