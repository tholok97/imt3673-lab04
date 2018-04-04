package com.example.tholok.lab04;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends Activity {

    private static String TAG = "UsersActivity";


    FirebaseDatabase database;
    DatabaseReference myRef;

    ListView lwUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);


        // setup componenets
        lwUsers = (ListView) findViewById(R.id.lw_users);

        // setup db
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("messages");

        final ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        lwUsers.setAdapter(arrayAdapter);

        lwUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String username = (String) adapterView.getItemAtPosition(i);

                // start webview activity with topic link
                // TEST: just start it
                Intent intent = new Intent(UsersActivity.this, ShowUserMessagesActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);

            }
        });

        myRef.orderByChild("u").addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        ArrayList<String> usersToShow = new ArrayList<>();

                        for (DataSnapshot data : dataSnapshot.getChildren()) {

                            Message message = data.getValue(Message.class);

                            // if not already added to list of users to show, add it
                            if (!usersToShow.contains(message.u)) {
                                usersToShow.add(message.u);
                            }

                        }


                        for (String username : usersToShow) {
                            arrayAdapter.add(username);
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
