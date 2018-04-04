package com.example.tholok.lab04;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity {

    public static boolean isVisible;

    private static String TAG = "MainActivity";

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    FirebaseDatabase database;
    DatabaseReference myRef;

    EditText edMessage;
    Button bMessage;
    ListView lwMessages;

    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get prefs
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();



        // setup components
        edMessage = (EditText) findViewById(R.id.et_message);
        bMessage = (Button) findViewById(R.id.b_message);
        lwMessages = (ListView) findViewById(R.id.lw_messages);

        // start fetcher service
        Intent serviceIntent = new Intent(this, MessageFetcherService.class);
        startService(serviceIntent);


        // setup listview
        arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1);
        lwMessages.setAdapter(arrayAdapter);


        // setup database// Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("messages");


        mAuth = FirebaseAuth.getInstance();

        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();


                            /*

                            // HAVE TO DO THIS HERE TO GUARANTEE AUTHENTICATED (dirty solution yea..)
                            // add listener to database messages-reference
                            myRef.addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    // for each message -> add it to the list

                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                        Message message = data.getValue(Message.class);
                                        Log.e(TAG, "message received: " + message.m);

                                        // update UI with new message
                                        arrayAdapter.add(message.u + ": " + message.m);

                                    }

                                    // scroll listview down
                                    lwMessages.setSelection(lwMessages.getCount()-1);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // not implemented
                                }
                            });

                            */

                            // add listener to database messages-reference
                            myRef.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                    // get message object from snapshot
                                    Message message = dataSnapshot.getValue(Message.class);

                                    // do some logging
                                    Log.d(TAG, "The new message is: " + message.m);

                                    // update UI with new message
                                    arrayAdapter.add(message.u + ": " + message.m);

                                    // scroll listview down
                                    lwMessages.setSelection(lwMessages.getCount()-1);


                                    // store date of most recent message
                                    if (isVisible) {
                                        SharedPreferences.Editor editor = prefs.edit();

                                        editor.putLong("latest-message-timestamp", Long.parseLong(message.d));
                                        editor.apply();
                                    }
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                    // not implemented
                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                    // not implemented
                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                    // not implemented
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // not implemented
                                }
                            });




                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });


        /* FIRST-TIME UPDATE OF THE LISTVIEW DOESN'T WORK IF I LEAVE THIS IN

        // if first time user -> show pref screen
        final Boolean isFirstTime = prefs.getBoolean("isFirstTime", true);
        if (isFirstTime) {
            editor.putBoolean("isFirstTime", false);
            editor.apply();
            Intent intent = new Intent(this, PickUsername.class);
            startActivity(intent);
        }
        */


    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    public void usersButton(View view) {
        Intent intent = new Intent(this, UsersActivity.class);
        startActivity(intent);
    }

    public void preferencesButton(View view) {
        Intent intent = new Intent(this, PickUsername.class);
        startActivity(intent);
    }

    public void sendButton(View view) {

        // get username
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String username = prefs.getString("username", "NULL");

        // get date
        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
        String formattedDate = s.format(new Date());

        // prepare message from tw_message
        Message message = new Message(
            formattedDate,
            username,
            edMessage.getText().toString()
        );

        // send message to realtime database
        DatabaseReference postRef = myRef.push();
        postRef.setValue(message);


        // clean up tw_message
        edMessage.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }
}
