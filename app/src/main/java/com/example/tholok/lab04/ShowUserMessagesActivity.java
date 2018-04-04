package com.example.tholok.lab04;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowUserMessagesActivity extends Activity {

    ListView lwUsersMessages;

    FirebaseDatabase database;
    DatabaseReference myRef;

    private static String TAG = "ShowUserMessagesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_messages);

        lwUsersMessages = (ListView) findViewById(R.id.lw_users_messages);

        // get url from intent
        Intent intent = getIntent();
        String username = intent.getExtras().getString("username");

        // setup db
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("messages");

        Log.e(TAG, "username to show for: " + username);


        final ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        lwUsersMessages.setAdapter(arrayAdapter);

        lwUsersMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String username = (String) adapterView.getItemAtPosition(i);

                // start webview activity with topic link
                // TEST: just start it
                Intent intent = new Intent(ShowUserMessagesActivity.this, ShowUserMessagesActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);

            }
        });

        myRef.orderByChild("u").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    Message message = data.getValue(Message.class);

                    arrayAdapter.add(message.u + ": " + message.m);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
