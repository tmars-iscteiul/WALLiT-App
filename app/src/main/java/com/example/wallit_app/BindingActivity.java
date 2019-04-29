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

    protected String username = "null";

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NetworkingService.MSG_ACK_POSITIVE:
                    System.out.println("Received positive ack from service: " + msg.arg1);
                    handlePositiveAck();
                    break;
                case NetworkingService.MSG_ACK_NEGATIVE:
                    System.out.println("Received negative ack from service: " + msg.arg1);
                    handleNegativeAck();
                    break;
                case NetworkingService.MSG_SEND_DATA:
                    System.out.println("Received from service: " + msg.arg1);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            mService = new Messenger(service);
            try {
                Message msg = Message.obtain(null, NetworkingService.MSG_BIND);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
            }
            System.out.println("Service connected");
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            System.out.println("Service disconnected");
        }
    };

    protected abstract void handlePositiveAck();

    protected abstract void handleNegativeAck();

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, NetworkingService.class), mConnection, Context.BIND_AUTO_CREATE);
        System.out.println("Networking service intent bound to " + this.toString());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mService != null) {
            try {
                Message msg = Message.obtain(null, NetworkingService.MSG_UNBIND);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
            }
        }
        unbindService(mConnection);
        System.out.println("Networking service intent unbound to " + this.toString());
        try {
            Message msg = Message.obtain(null, NetworkingService.MSG_TERMINATE_SERVICE);
            msg.replyTo = mMessenger;
            mService.send(msg);
        } catch (RemoteException e) {
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

}
