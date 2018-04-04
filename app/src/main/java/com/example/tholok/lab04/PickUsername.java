package com.example.tholok.lab04;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;

public class PickUsername extends Activity {

    EditText edUsername;
    EditText edFetchRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_username);

        // setup componenets
        edUsername = (EditText) findViewById(R.id.et_username);
        edFetchRate = (EditText) findViewById(R.id.et_fetch_rate);

        loadUI();
    }

    /**
     * Load UI from prefs
     */
    private void loadUI() {

        // get prefs
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // set components
        edUsername.setText(prefs.getString("username", "NULL"));
        edFetchRate.setText(Integer.toString(prefs.getInt("fetch-rate", 1)));
    }

    /**
     * Save UI to prefs
     */
    private void saveUI() {

        // get prefs editor
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = prefs.edit();

        // put UI into prefs
        editor.putString("username", edUsername.getText().toString());
        editor.putInt("fetch-rate", Integer.parseInt(edFetchRate.getText().toString()));
        editor.apply();
    }

    public void finishedButton(View view) {

        saveUI();

        finish();
    }
}
