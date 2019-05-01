package com.example.wallit_app.networking;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;


import com.example.wallit_app.LoginActivity;
import com.example.wallit_app.R;

import java.util.ArrayList;

public class NetworkingService extends Service {

    private String host = "192.168.1.8";    // Static host hardcoded here. TODO: Add a manual input in LoginActivity

    private ArrayList<Messenger> mClients = new ArrayList<>();
    private int mValue = 0;

    private ServerConnectionHandler connectionHandler;
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // Handles message sent from activities
            switch (ServiceMessages.getMessageByID(msg.what)) {
                case MSG_LOGIN:
                    mClients.add(msg.replyTo);
                    connectionHandler.sendDataToServer("login,"+msg.obj);
                    break;
                case MSG_BIND:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNBIND:
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_SEND_DATA:
                    mValue = msg.arg1;
                    connectionHandler.sendDataToServer((String)msg.obj);
                    //System.out.println("Received data from activity " + mValue + ": \""  + msg.obj + "\".");
                    break;
                case MSG_TERMINATE_SERVICE:
                    terminateConnection();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onCreate()   {
        if(connectionHandler == null)   {
            connectionHandler = new ServerConnectionHandler(this, host);
            connectionHandler.start();
            Toast.makeText(this, "Connected to server.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        connectionHandler.terminateConnection();
        Toast.makeText(this, "Disconnected from server.", Toast.LENGTH_SHORT).show();
    }

    // Handles ack sent from the server.
    public void returnAckToActivity(String rawAck)    {
        Message msg = getMessageFromAck(rawAck);
        for (int i = mClients.size()-1; i>=0; i--) {
            try {
                mClients.get(i).send(msg);
            } catch (RemoteException e) {
                mClients.remove(i);
            }
        }
    }

    private Message getMessageFromAck(String rawAck)    {
        int ackCode = ServiceMessages.getMessageByString(rawAck).getMessageID();
        // Add obj to message if ever applicable (Will be once we implement the fund information return)
        // Perhaps add an if(ackCode == MSG_SEND_DATA) and do work there
        return Message.obtain(null, ackCode, this.hashCode());
    }

    public ServerConnectionHandler getConnectionHandler()   {
        return connectionHandler;
    }

    public void terminateConnection()   {
        stopSelf();
    }

}