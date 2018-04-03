package com.example.tholok.lab04;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import static java.lang.Thread.sleep;

public class MessageFetcherService extends Service {

    private String TAG = "MessageFetcherService";

    public MessageFetcherService() {

        DispatcherTask dispatcherTask = new  DispatcherTask();
        dispatcherTask.execute();
        
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

                Log.d(TAG, "Pref milli: " + Integer.toString(milliseconds));

                // try and sleep
                try {
                    sleep(milliseconds);
                } catch (Exception ex) {
                    Log.d(TAG, "Couldn't sleep.. : " + ex.getMessage());
                }

                // do work
                Log.d(TAG, "DOING WORK");

                // if app isn't running -> check for messages
                /* TODO: implement check if app active */
                checkForNewMessages();

            }
        }
    }

    /**
     *  Check for new messages. If there are any -> make notification about it that sends user to
     *  messages screen
     */
    private void checkForNewMessages() {

    }
}
