package com.cameronfranz.robotremote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static String EXTRA_MESSAGE = "com.cameronfranz.dronecontrol.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startController(View view) {
        Intent intent = new Intent(this,ControllerActivity.class);
        startActivity(intent);

    }

    public void startSettings(View view) {
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }
}


