package com.cameronfranz.robotremote;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class ControllerActivity extends AppCompatActivity {

    //output to UDP and input from UDP respectively
    public Map<String,Integer> ctrl_state_outgoing;
    public Map<String,String> ctrl_state_incoming;
    public static int TOUCHPAD_RANGE = 100;
    public UDPCommandServer commandServer;
    public Thread commandThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctrl_state_outgoing = new HashMap<String,Integer>();
        initializeDefaultState();
        setContentView(R.layout.activity_controller);
        FrameLayout ctrl_leftTouchPad = (FrameLayout) findViewById(R.id.ctrl_leftTouchPad);
        FrameLayout ctrl_rightTouchPad = (FrameLayout) findViewById(R.id.ctrl_rightTouchPad);

        SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences(this);
        String controller_ip = spref.getString("controller_ip","192.168.1.1");
        int controller_port = spref.getInt("controller_port",12345);

        try {
            commandServer = new UDPCommandServer(ctrl_state_outgoing, ctrl_state_incoming);
            commandServer.setDestination(controller_ip, controller_port);
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }

        commandThread = new Thread(commandServer);
        commandThread.start();

        ctrl_leftTouchPad.setOnTouchListener(touchPadHandler);
        ctrl_rightTouchPad.setOnTouchListener(touchPadHandler);

        findViewById(R.id.ctrl_btn1).setOnTouchListener(buttonsHandler);
        findViewById(R.id.ctrl_btn2).setOnTouchListener(buttonsHandler);
        findViewById(R.id.ctrl_btn3).setOnTouchListener(buttonsHandler);
        findViewById(R.id.ctrl_btn4).setOnTouchListener(buttonsHandler);
        findViewById(R.id.ctrl_btn5).setOnTouchListener(buttonsHandler);
        findViewById(R.id.ctrl_btn6).setOnTouchListener(buttonsHandler);

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
                    ctrl_state_outgoing.put("leftTouchPadX",normalizedXPos);
                    ctrl_state_outgoing.put("leftTouchPadY",normalizedYPos);
                    break;
                case R.id.ctrl_rightTouchPad:
                    ctrl_state_outgoing.put("rightTouchPadX",normalizedXPos);
                    ctrl_state_outgoing.put("rightTouchPadY",normalizedYPos);
                    break;

            }
            updateTouchPadMarkers();

            return true;
        }
    };

    private View.OnTouchListener buttonsHandler = new View.OnTouchListener(){
        public boolean onTouch(View view, MotionEvent event) {
            int btnVal;
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                btnVal = 1;
            }
            else {btnVal = 0;
            }


            switch (view.getId()) {
                case R.id.ctrl_btn1:
                    ctrl_state_outgoing.put("btn1",btnVal);
                    break;
                case R.id.ctrl_btn2:
                    ctrl_state_outgoing.put("btn2",btnVal);
                    break;
                case R.id.ctrl_btn3:
                    ctrl_state_outgoing.put("btn3",btnVal);
                    break;
                case R.id.ctrl_btn4:
                    ctrl_state_outgoing.put("btn4",btnVal);
                    break;
                case R.id.ctrl_btn5:
                    ctrl_state_outgoing.put("btn5",btnVal);
                    break;
                case R.id.ctrl_btn6:
                    ctrl_state_outgoing.put("btn6",btnVal);
                    break;
            }

            //Log.d("Touch",Integer.toString(ctrl_state_outgoing.get("btn1")));

            return false;
        }
    };



    public void initializeDefaultState() {
        ctrl_state_outgoing.put("leftTouchPadX",0);
        ctrl_state_outgoing.put("leftTouchPadY",0);
        ctrl_state_outgoing.put("rightTouchPadX",0);
        ctrl_state_outgoing.put("rightTouchPadY",0);
        ctrl_state_outgoing.put("btn1",0);
        ctrl_state_outgoing.put("btn2",0);
        ctrl_state_outgoing.put("btn3",0);
        ctrl_state_outgoing.put("btn4",0);
        ctrl_state_outgoing.put("btn5",0);
        ctrl_state_outgoing.put("btn6",0);

    }

    public void updateTelemetry() {}

    public void updateTouchPadMarkers() {
        View lPad = findViewById(R.id.ctrl_leftTouchPad);
        View lMarker = findViewById(R.id.leftTouchPadMarker);
        lMarker.setX(lPad.getWidth() * ctrl_state_outgoing.get("leftTouchPadX")
                /(TOUCHPAD_RANGE*2) + lPad.getWidth()/2 - lMarker.getWidth()/2);
        lMarker.setY(lPad.getHeight() * -ctrl_state_outgoing.get("leftTouchPadY")
                /(TOUCHPAD_RANGE*2) + lPad.getHeight()/2 - lMarker.getHeight()/2);

        View rPad = findViewById(R.id.ctrl_rightTouchPad);
        View rMarker = findViewById(R.id.rightTouchPadMarker);
        rMarker.setX(ctrl_state_outgoing.get("rightTouchPadX")*rPad.getWidth()
                /(TOUCHPAD_RANGE*2) + rPad.getWidth()/2 - rMarker.getWidth()/2);
        rMarker.setY(-ctrl_state_outgoing.get("rightTouchPadY")*rPad.getHeight()
                /(TOUCHPAD_RANGE*2) + rPad.getHeight()/2 - rMarker.getHeight()/2);


    }

    public void onStop() {
        super.onStop();
        commandThread.interrupt();
    }

    public int limitRange(int n, int min, int max) {
        if (n>max) return max;
        else if (n<min) return min;
        else return n;
    }

}
