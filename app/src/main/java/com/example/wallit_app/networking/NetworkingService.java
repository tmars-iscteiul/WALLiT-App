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
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;


import com.example.wallit_app.R;

import java.util.ArrayList;

public class NetworkingService extends Service {

    public static final int MSG_UNBIND = -1;
    public static final int MSG_BIND = 0;
    public static final int MSG_ACK_POSITIVE = 1;
    public static final int MSG_ACK_NEGATIVE = 2;
    public static final int MSG_SEND_DATA = 3;
    public static final int MSG_TERMINATE_SERVICE = 4;



    private ArrayList<Messenger> mClients = new ArrayList<Messenger>();
    private int mValue = 0;

    private ServerConnectionHandler connectionHandler;
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_BIND:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNBIND:
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_SEND_DATA:
                    mValue = msg.arg1;
                    connectionHandler.sendDataToServer((String)msg.obj);
                    System.out.println("Received data from activity " + mValue + ": \""  + msg.obj + "\".");
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
    public void onCreate() {
        connectionHandler = new ServerConnectionHandler(this);
        connectionHandler.start();
        Toast.makeText(this, "Connected to server", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        connectionHandler.terminateConnection();
        Toast.makeText(this, "Disconnected from server", Toast.LENGTH_SHORT).show();
    }

    // TODO Duplicated code here and bellow
    public void returnAckToActivity(int networkingServiceMsg)    {
        Message msg = Message.obtain(null, networkingServiceMsg, this.hashCode());
        for (int i = mClients.size()-1; i>=0; i--) {
            try {
                mClients.get(i).send(msg);
            } catch (RemoteException e) {
                mClients.remove(i);
            }
        }
    }

    protected void sendDataToBoundActivities(String data) throws RemoteException   {
        Message msg = Message.obtain(null, NetworkingService.MSG_SEND_DATA, this.hashCode());
        msg.obj = data;
        mValue = msg.arg1;
        for (int i = mClients.size()-1; i>=0; i--) {
            try {
                mClients.get(i).send(msg);
            } catch (RemoteException e) {
                mClients.remove(i);
            }
        }
    }

    public ServerConnectionHandler getConnectionHandler()   {
        return connectionHandler;
    }

    public void terminateConnection()   {
        stopSelf();
    }

}