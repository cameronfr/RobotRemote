package com.cameronfranz.robotremote;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class ControllerActivity extends AppCompatActivity {

    //output to UDP and input from UDP respectively
    private Map<String,Integer> ctrlStateOutgoing;
    public static int TOUCHPAD_RANGE = 100;
    private UDPSendServer sendServer;
    private Thread sendThread;
    private UDPReceiveServer receiveServer;
    private Thread receiveThread;
    TextView telemetryText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.d("CommandServer","oncreate called");
        super.onCreate(savedInstanceState);
        ctrlStateOutgoing = new HashMap<String,Integer>();
        setContentView(R.layout.activity_controller);
        initializeDefaultState();

        SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences(this);
        String controller_ip = spref.getString("controller_ip","192.168.1.1");
        int sendPort = spref.getInt("controller_send_port",8111);
        int recievePort = spref.getInt("controller_receive_port",8112);
        int updateRate = spref.getInt("controller_update_rate",60);


        sendServer = new UDPSendServer(ctrlStateOutgoing);
        sendServer.setDestination(controller_ip, sendPort);
        sendServer.setUpdateRate(updateRate);

        receiveServer = new UDPReceiveServer(new UDPReceiveServer.RecievedMessageListener() {
            @Override
            public void onRecieveMessage(String message) {
                updateTelemetryText(message);
            }
        });
        receiveServer.setReceivePort(recievePort);

        sendThread = new Thread(sendServer);
        receiveThread = new Thread(receiveServer);
        sendThread.start();
        receiveThread.start();

        telemetryText = (TextView) findViewById(R.id.telemetryText);
        FrameLayout ctrl_leftTouchPad = (FrameLayout) findViewById(R.id.ctrl_leftTouchPad);
        FrameLayout ctrl_rightTouchPad = (FrameLayout) findViewById(R.id.ctrl_rightTouchPad);
        ctrl_leftTouchPad.setOnTouchListener(touchPadHandler);
        ctrl_rightTouchPad.setOnTouchListener(touchPadHandler);
        findViewById(R.id.ctrl_btn1).setOnTouchListener(tapButtonsHandler);
        findViewById(R.id.ctrl_btn2).setOnTouchListener(tapButtonsHandler);
        findViewById(R.id.ctrl_btn3).setOnTouchListener(tapButtonsHandler);
        findViewById(R.id.ctrl_btn4).setOnTouchListener(tglButtonsHandler);
        findViewById(R.id.ctrl_btn5).setOnTouchListener(tglButtonsHandler);
        findViewById(R.id.ctrl_btn6).setOnTouchListener(tglButtonsHandler);

    }
    private View.OnTouchListener touchPadHandler = new View.OnTouchListener(){
        public boolean onTouch(View view, MotionEvent event) {
            float relativeXPos = event.getX();
            float relativeYPos = event.getY();

            //Centered and normalized to +- 100
            int normalizedXPos = (int) ((event.getX() - view.getWidth()/2)
                    *TOUCHPAD_RANGE*2/view.getWidth());
            int normalizedYPos = (int) ((-(event.getY()-view.getHeight()/2))
                    *TOUCHPAD_RANGE*2/view.getHeight());

            normalizedXPos = limitRange(normalizedXPos,-TOUCHPAD_RANGE,TOUCHPAD_RANGE);
            normalizedYPos = limitRange(normalizedYPos,-TOUCHPAD_RANGE,TOUCHPAD_RANGE);


            switch (view.getId()) {
                case R.id.ctrl_leftTouchPad:
                    ctrlStateOutgoing.put("'leftTouchPadX'",normalizedXPos);
                    ctrlStateOutgoing.put("'leftTouchPadY'",normalizedYPos);
                    break;
                case R.id.ctrl_rightTouchPad:
                    ctrlStateOutgoing.put("'rightTouchPadX'",normalizedXPos);
                    ctrlStateOutgoing.put("'rightTouchPadY'",normalizedYPos);
                    break;

            }
            updateTouchPadMarkers();
            return true;
        }
    };

    private View.OnTouchListener tapButtonsHandler = new View.OnTouchListener(){
        public boolean onTouch(View view, MotionEvent event) {
            int btnVal;
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                btnVal = 1;
            }
            else {btnVal = 0;
            }


            switch (view.getId()) {
                case R.id.ctrl_btn1:
                    ctrlStateOutgoing.put("'btn1'", btnVal);
                    break;
                case R.id.ctrl_btn2:
                    ctrlStateOutgoing.put("'btn2'", btnVal);
                    break;
                case R.id.ctrl_btn3:
                    ctrlStateOutgoing.put("'btn3'", btnVal);
                    break;
            }

            //Log.d("Touch",Integer.toString(ctrl_state_outgoing.get("btn1")));

            return false;
        }
    };

    private View.OnTouchListener tglButtonsHandler = new View.OnTouchListener(){
        public boolean onTouch(View view, MotionEvent event) {
            //Opposite, because the event is triggered before the button changes state
            int btnVal = !((ToggleButton)view).isChecked() ? 1 : 0;

            switch (view.getId()) {
                case R.id.ctrl_btn4:
                    ctrlStateOutgoing.put("'btn4'",btnVal);
                    break;
                case R.id.ctrl_btn5:
                    ctrlStateOutgoing.put("'btn5'",btnVal);
                    break;
                case R.id.ctrl_btn6:
                    ctrlStateOutgoing.put("'btn6'",btnVal);
                    break;
            }

            return false;
        }
    };

    public void initializeDefaultState() {
        ctrlStateOutgoing.put("'leftTouchPadX'",0);
        ctrlStateOutgoing.put("'leftTouchPadY'",0);
        ctrlStateOutgoing.put("'rightTouchPadX'",0);
        ctrlStateOutgoing.put("'rightTouchPadY'",0);
        ctrlStateOutgoing.put("'btn1'",0);
        ctrlStateOutgoing.put("'btn2'",0);
        ctrlStateOutgoing.put("'btn3'",0);
        ctrlStateOutgoing.put("'btn4'",0);
        ctrlStateOutgoing.put("'btn5'",0);
        ctrlStateOutgoing.put("'btn6'",0);

    }

    public void updateTouchPadMarkers() {
        View lPad = findViewById(R.id.ctrl_leftTouchPad);
        View lMarker = findViewById(R.id.leftTouchPadMarker);
        lMarker.setX(lPad.getWidth() * ctrlStateOutgoing.get("'leftTouchPadX'")
                /(TOUCHPAD_RANGE*2) + lPad.getWidth()/2 - lMarker.getWidth()/2);
        lMarker.setY(lPad.getHeight() * -ctrlStateOutgoing.get("'leftTouchPadY'")
                /(TOUCHPAD_RANGE*2) + lPad.getHeight()/2 - lMarker.getHeight()/2);

        View rPad = findViewById(R.id.ctrl_rightTouchPad);
        View rMarker = findViewById(R.id.rightTouchPadMarker);
        rMarker.setX(ctrlStateOutgoing.get("'rightTouchPadX'")*rPad.getWidth()
                /(TOUCHPAD_RANGE*2) + rPad.getWidth()/2 - rMarker.getWidth()/2);
        rMarker.setY(-ctrlStateOutgoing.get("'rightTouchPadY'")*rPad.getHeight()
                /(TOUCHPAD_RANGE*2) + rPad.getHeight()/2 - rMarker.getHeight()/2);


    }

    public void updateTelemetryText (final String s) {
        Handler mainHandler = new Handler(Looper.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                //Log.d("CommandServer","View height: " + ((View)telemetryText.getParent()).getHeight());
                //Log.d("CommandServer","Text Height:" + Integer.toString(telemetryText.getHeight()));
                telemetryText.setText(s);

                if (telemetryText.getHeight() == ((View)telemetryText.getParent()).getHeight()) {
                    //Log.d("CommandServer",telemetryText.getTextSize() + " " + Integer.toString(telemetryText.getHeight()));
                    telemetryText.setTextSize(TypedValue.COMPLEX_UNIT_PX,telemetryText.getTextSize()-1);
                }
            }
        };
        mainHandler.post(myRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d("CommandServer","Activity destroyed, killing servers...");
        sendThread.interrupt();
        receiveThread.interrupt();
        while (sendThread.isAlive() || receiveThread.isAlive()) {

        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit the controller?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public int limitRange(int n, int min, int max) {
        if (n>max) return max;
        else if (n<min) return min;
        else return n;
    }

}

