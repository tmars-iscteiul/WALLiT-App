package com.example.wallit_app;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.wallit_app.networking.NetworkingService;


public abstract class BindingActivity extends AppCompatActivity {

    protected Messenger mService = null;
    protected final Messenger mMessenger = new Messenger(new IncomingHandler());

    private boolean boundToNetworkingService = false;
    protected boolean loginOnBind = false;
    protected String username = "%NOT_SET%";

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NetworkingService.MSG_ACK_POSITIVE:
                    System.out.println("Received positive ack from the networking service.");
                    handlePositiveAck();
                    break;
                case NetworkingService.MSG_ACK_NEGATIVE:
                    System.out.println("Received negative ack from the networking service.");
                    handleNegativeAck();
                    break;
                case NetworkingService.MSG_SEND_DATA:
                    System.out.println("Received from service: " + msg.arg1);
                    // TODO: Finish this
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    protected ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been established.
            mService = new Messenger(service);
            Message msg;
            if(loginOnBind) {
                msg = Message.obtain(null, NetworkingService.MSG_LOGIN);
                msg.obj = username;
            }   else    {
                msg = Message.obtain(null, NetworkingService.MSG_BIND);
            }
            try {
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
            }
            System.out.println(this.toString() + " activity connected to the networking service.");
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            System.out.println(this.toString() + " activity disconnected to the networking service.");
        }
    };

    protected abstract void handlePositiveAck();

    protected abstract void handleNegativeAck();

    @Override
    protected void onStop() {
        super.onStop();
        unbindToNetworkingService();
        terminateConnectionHandler();   // TODO: Debug this later. Currently, each activity is closing the connection, but it's not really going through? Why?
    }

    protected void logoutUser()  {
        try {
            Message msg = Message.obtain(null, NetworkingService.MSG_LOGOUT, this.hashCode());
            msg.obj = username;
            mService.send(msg);
        } catch (RemoteException e) {
            e.getStackTrace();
        }
    }

    protected void redirectDataToServer(String data)   {
        try {
            Message msg = Message.obtain(null, NetworkingService.MSG_SEND_DATA, this.hashCode());
            msg.obj = data;
            mService.send(msg);
        } catch (RemoteException e) {
            e.getStackTrace();
        }
    }

    protected void bindToNetworkingService()    {
        Intent intent = new Intent(this, NetworkingService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        boundToNetworkingService = true;
        // TODO: Set host here from method inputs
        System.out.println("Networking service intent bound to " + this.toString());
    }

    protected void unbindToNetworkingService()  {
        if(boundToNetworkingService)    {
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, NetworkingService.MSG_UNBIND);
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

    private void terminateConnectionHandler()   {
        try {
            Message msg = Message.obtain(null, NetworkingService.MSG_TERMINATE_SERVICE);
            msg.replyTo = mMessenger;
            mService.send(msg);
        } catch (RemoteException e) {
            e.getStackTrace();
        }
    }

}
