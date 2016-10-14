package com.cameronfranz.robotremote;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * Created by cameronfranz on 7/13/16.
 */
public class UDPCommandServer implements Runnable {

    public Map<String,Integer> ctrl_state_outgoing;
    public Map<String,String> ctrl_state_incoming;
    public String destinationIP;
    public int destinationPort;
    public int updateRate = 60;


    public UDPCommandServer(Map<String,Integer> outgoingMap,Map<String,String> incomingMap) {
        ctrl_state_outgoing = outgoingMap;
        ctrl_state_incoming = incomingMap;
    }

    public void setDestination(String ip,int port) throws UnknownHostException {
        destinationIP = ip;
        destinationPort = port;
    }

    public void setUpdateRate (int rate) {
        updateRate = rate;
    }

    public void run() {
        DatagramSocket sendSocket;
        DatagramSocket receiveSocket;
        Log.d("CommandServer","Starting UDP server with IP destination: " + destinationIP +
                " and port: " + Integer.toString(destinationPort));
        try {
            sendSocket = new DatagramSocket();
            //receiveSocket = new DatagramSocket(destinationPort);
            while(!Thread.currentThread().isInterrupted()) {
                sendCommandPacket(sendSocket,ctrl_state_outgoing.toString());
                //receiveCommandPacket(receiveSocket);
                Thread.sleep(100);
                Log.d("CommandThread",ctrl_state_outgoing.toString());
            }
            sendSocket.close();
            //receiveSocket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendCommandPacket (DatagramSocket socket, String message) throws IOException {
        byte[] sendData = new byte[1024];
        sendData = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,InetAddress.getByName(destinationIP),destinationPort);
        socket.send(sendPacket);
    }

    //Need to put this in seperate thread, because will hang
    public String receiveCommandPacket(DatagramSocket socket) throws IOException {
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);
        String message = new String(receiveData, 0, receivePacket.getLength());
        Log.d("CommandServer",receivePacket.getAddress().getHostName() + ": "
                + message);
        return message;
    }

    public void updateState(String recievedPacket) {

    }
}
