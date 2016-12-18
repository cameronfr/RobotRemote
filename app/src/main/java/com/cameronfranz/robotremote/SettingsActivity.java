package com.cameronfranz.robotremote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditText ipAddress = (EditText) findViewById(R.id.settingsIP);
        EditText sendPort = (EditText) findViewById(R.id.settingsSendPort);
        EditText receivePort = (EditText) findViewById(R.id.settingsReceivePort);
        EditText updateRate = (EditText) findViewById(R.id.settingsUpdateRate);

        SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences(this);

        ipAddress.setText(spref.getString("controller_ip",""));
        sendPort.setText(Integer.toString(spref.getInt("controller_send_port",8111)));
        receivePort.setText(Integer.toString(spref.getInt("controller_receive_port",8112)));
        updateRate.setText(Integer.toString(spref.getInt("controller_update_rate",60)));
    }

    public void saveSettings(View view) {
        EditText ipAddress = (EditText) findViewById(R.id.settingsIP);
        EditText sendPort = (EditText) findViewById(R.id.settingsSendPort);
        EditText receivePort = (EditText) findViewById(R.id.settingsReceivePort);
        EditText updateRate = (EditText) findViewById(R.id.settingsUpdateRate);

        String ipAddressString;
        int portInt;
        try{
            SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = spref.edit();

            editor.putString("controller_ip",ipAddress.getText().toString());
            editor.putInt("controller_send_port",Integer.parseInt(sendPort.getText().toString()));
            editor.putInt("controller_receive_port",Integer.parseInt(receivePort.getText().toString()));
            editor.putInt("controller_update_rate",Integer.parseInt(updateRate.getText().toString()));

            editor.commit();

        }
        catch (Exception e) {
            e.printStackTrace();
            Context context = getApplicationContext();
            CharSequence text = "Error: Inputted settings are invalid!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }

        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);


    }
}
