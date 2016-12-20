package com.cameronfranz.robotremote;

import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import android.content.Context;


/**
 * Created by cameronfranz on 7/13/16.
 */
public class UDPSendServer implements Runnable {

    private Map<String,Integer> ctrl_state_outgoing;
    private String destinationIP;
    private int destinationPort;
    private int updateRate;


    public UDPSendServer(Map<String,Integer> outgoingMap) {
        ctrl_state_outgoing = outgoingMap;
    }

    public void setDestination(String ip,int port) {
        destinationIP = ip;
        destinationPort = port;
    }

    public void setUpdateRate (int rate) {
        updateRate = rate;
    }

    public void run() {
        DatagramSocket sendSocket;
        Log.d("CommandServer","Starting UDP send server with IP destination: " + destinationIP +
                " and port: " + Integer.toString(destinationPort));
        try {
            sendSocket = new DatagramSocket();
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    String stringOut = ctrl_state_outgoing.toString().replace("=", ":");
                    sendCommandPacket(sendSocket, stringOut);
                }
                catch (UnknownHostException e) {
                    Log.d("CommandServer","Invalid destination IP");
                    e.printStackTrace();
                }
                catch (IOException e) {
                    Log.d("CommandServer","Error sending packet");
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000 / updateRate);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            Log.d("CommandServer","Shutting down UDP send server...");
            sendSocket.close();
        }
        catch (Exception e) {
            Log.d("CommandServer","Error binding send socket");
            e.printStackTrace();
        }
    }

    private void sendCommandPacket (DatagramSocket socket, String message) throws IOException, UnknownHostException{
        byte[] sendData = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,InetAddress.getByName(destinationIP),destinationPort);
        socket.send(sendPacket);
        //Log.d("CommandThread","Sent message: " + stringOut);
    }



}
