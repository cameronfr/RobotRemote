package com.cameronfranz.robotremote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditText ipAddress = (EditText) findViewById(R.id.settingsIP);
        EditText port = (EditText) findViewById(R.id.settingsPort);

        SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences(this);

        ipAddress.setText(spref.getString("controller_ip",""));
        port.setText(Integer.toString(spref.getInt("controller_port",8000)));

    }

    public void saveSettings(View view) {
        EditText ipAddress = (EditText) findViewById(R.id.settingsIP);
        EditText port = (EditText) findViewById(R.id.settingsPort);
        String ipAddressString;
        int portInt;
        try{
            ipAddressString = ipAddress.getText().toString();
            portInt =  Integer.parseInt(port.getText().toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }

        SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = spref.edit();
        editor.putString("controller_ip",ipAddressString);
        editor.putInt("controller_port",portInt);
        editor.commit();

        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);


    }
}
