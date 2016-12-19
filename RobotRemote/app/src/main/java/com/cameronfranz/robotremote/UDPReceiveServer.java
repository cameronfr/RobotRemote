package com.cameronfranz.robotremote;

import android.util.Log;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPReceiveServer implements Runnable {

    private int receivePort;
    RecievedMessageListener messageListener;
    static int SOCKET_TIMEOUT = 1000;

    public UDPReceiveServer(RecievedMessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public void setReceivePort(int receivePort) {
        this.receivePort = receivePort;
    }

    public void run() {
        DatagramSocket receiveSocket;
        Log.d("CommandServer","Starting UDP receive server with port: " +
                Integer.toString(receivePort));
        try {
            receiveSocket = new DatagramSocket(receivePort);
            receiveSocket.setSoTimeout(SOCKET_TIMEOUT);
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    String message = receiveCommandPacket(receiveSocket);
                    messageListener.onRecieveMessage(message);
                }
                catch (IOException e) {

                }
                try {
                    Thread.sleep(1);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            Log.d("CommandServer","Shutting down receive server...");
            receiveSocket.close();
        }
        catch (Exception e) {
            Log.d("CommandServer","Error binding receive socket");
            e.printStackTrace();
        }
    }

    private String receiveCommandPacket(DatagramSocket socket) throws IOException {
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);
        String message = new String(receiveData, 0, receivePacket.getLength());
        //Log.d("CommandServer","Received Packet: "+ receivePacket.getAddress().getHostName() + ": " + message);
        return message;
    }

    public interface RecievedMessageListener {
        void onRecieveMessage (String message);
    }


}