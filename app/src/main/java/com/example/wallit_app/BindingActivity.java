package com.example.wallit_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;

import com.example.wallit_app.networking.NetworkingService;
import com.example.wallit_app.networking.ServiceMessages;


public abstract class BindingActivity extends AppCompatActivity {

    protected Messenger mService = null;
    protected final Messenger mMessenger = new Messenger(new IncomingHandler());

    private boolean boundToNetworkingService = false;
    protected boolean loginOnBind = false;
    protected boolean userLoggedIn = false;
    protected String username = "%NOT_SET%";

    protected AlertDialog.Builder alertDialogBuilder;
    protected AlertDialog alertDialog;
    protected ProgressDialog progressDialog;    // TODO Progress dialogs can be clicked out, find a way to block it

    // Handles message sent from the service
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            ServiceMessages sm = ServiceMessages.getMessageByID(msg.what);
            switch (sm) {
                case MSG_SEND_DATA:
                    handleDataAck(sm, (String)msg.obj);
                    break;
                default:
                    handleAck(sm);
                    super.handleMessage(msg);   // What does this do? Test with and without
                    break;
            }
        }
    }

    protected ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been established.
            mService = new Messenger(service);
            Message msg;
            if(loginOnBind) {
                msg = Message.obtain(null, ServiceMessages.MSG_LOGIN.getMessageID());
                msg.obj = username;
            }   else    {
                msg = Message.obtain(null, ServiceMessages.MSG_BIND.getMessageID());
            }
            try {
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
            }
            System.out.println(this.toString() + " activity's messaging system connected to the networking service.");
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            System.out.println(this.toString() + " activity's messaging system disconnected to the networking service.");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)   {
        super.onCreate(savedInstanceState);
        alertDialogBuilder = new AlertDialog.Builder(this);
        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindToNetworkingService();
    }

    // Called by incomingHandler after receiving an ack from the service/server
    protected abstract void handleAck(ServiceMessages ackCode);

    // Called by incomingHandler after receiving an ack with data from the service/server
    protected abstract void handleDataAck(ServiceMessages ackCode, String data);


    // Send a message to the service, with the intent of sending data: a constructed string for now.
    protected void redirectDataToServer(String data)   {
        try {
            Message msg = Message.obtain(null, ServiceMessages.MSG_SEND_DATA.getMessageID(), this.hashCode());
            msg.obj = data;
            mService.send(msg);
        } catch (RemoteException e) {
            e.getStackTrace();
        }
    }


    protected void bindToNetworkingService()    {
        Intent intent = new Intent(this, NetworkingService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        boundToNetworkingService = true;
        // TODO: Set host here from method inputs
        System.out.println("Networking service intent bound to " + this.toString());
    }

    protected void unbindToNetworkingService()  {
        if(boundToNetworkingService)    {
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, ServiceMessages.MSG_UNBIND.getMessageID());
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    e.getStackTrace();
                }
            }
            unbindService(mConnection);
            System.out.println("Networking service intent unbound to " + this.toString());
            boundToNetworkingService = false;
        }
    }

    public void showMessageDialog(String text) {
        alertDialogBuilder.setMessage(text)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
