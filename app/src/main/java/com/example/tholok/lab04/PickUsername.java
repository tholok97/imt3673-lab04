package com.example.tholok.lab04;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class PickUsername extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_username);
    }

    public void finishedButton(View view) {

        finish();
    }
}
