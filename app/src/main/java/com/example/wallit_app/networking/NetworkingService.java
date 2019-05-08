package com.example.wallit_app.networking;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import com.example.wallit_app.BindingActivity;

import java.util.ArrayList;

public class NetworkingService extends Service {


    private ArrayList<Messenger> mClients = new ArrayList<>();
    private int mValue = 0;

    private boolean offlineMode = false;
    private ServerConnectionHandler connectionHandler;
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    // Handles message sent from activities
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (ServiceMessages.getMessageByID(msg.what)) {
                case MSG_LOGIN:
                    mClients.add(msg.replyTo);
                    if(!offlineMode) {
                        connectionHandler.sendDataToServer("login,"+msg.obj);
                        connectionHandler.start();
                    }   else    {
                        returnAckToActivity("MSG_OFFLINE_ACK");
                    }
                    break;
                case MSG_BIND:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNBIND:
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_SEND_DATA:
                    mValue = msg.arg1;
                    if(!offlineMode) {
                        connectionHandler.sendDataToServer((String)msg.obj);
                    }   else    {
                        returnAckToActivity("MSG_OFFLINE_ACK");
                    }
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
        if(connectionHandler == null) {
            if(intent.getStringExtra(BindingActivity.CONNECTION_HOST).equals("offline"))
                offlineMode = true;
            else
                connectionHandler = new ServerConnectionHandler(this, intent.getStringExtra(BindingActivity.CONNECTION_HOST));
        }
        return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        if(connectionHandler != null) {
            if(connectionHandler.isConnected())
                Toast.makeText(this, "Disconnected from server.", Toast.LENGTH_SHORT).show();
            connectionHandler.terminateConnection();
        }
    }

    // Handles ack sent from the server, redirecting it to any bound activities (only one SHOULD BE bound at a time)
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

    // Consults the ServiceMessages enum to convert a rawAck string into a coherent ServiceMessage value type.
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