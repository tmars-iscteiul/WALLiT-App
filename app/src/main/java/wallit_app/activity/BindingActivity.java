package wallit_app.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.view.KeyEvent;

import java.lang.reflect.Field;

import wallit_app.networking.NetworkingService;
import wallit_app.utilities.ServiceMessages;


public abstract class BindingActivity extends AppCompatActivity {

    public static final String CONNECTION_HOST = "com.example.wallit_app.CONNECTIONHOST";

    protected Messenger mService = null;
    protected final Messenger mMessenger = new Messenger(new IncomingHandler());

    private boolean boundToNetworkingService = false;
    protected boolean loginOnBind = false;
    protected boolean userLoggedIn = false;
    protected String username = "%NOT_SET%";
    protected String host = "192.168.1.8";

    protected AlertDialog.Builder alertDialogBuilder;
    protected AlertDialog alertDialog;
    protected ProgressDialog progressDialog;    // TODO Progress dialogs can be clicked out, find a way to block it

    // Handles message sent from the service
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            ServiceMessages sm = ServiceMessages.getMessageByID(msg.what);
            switch (sm) {
                case MSG_ACK_FUND_DATA:
                case MSG_ACK_USER_DATA:
                    System.out.println("Received data from service: " + msg.obj);
                    handleDataAck(sm, msg.obj);
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
            // This is called when the connection with the service has been established (after binding request).
            mService = new Messenger(service);
            Message msg;
            if(loginOnBind) {
                msg = Message.obtain(null, ServiceMessages.MSG_LOGIN.getMessageID());
                msg.obj = ServiceMessages.REQUEST_LOGIN.getMessageString() + "," + username;
            }   else    {
                msg = Message.obtain(null, ServiceMessages.MSG_BIND.getMessageID());
            }
            try {
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
            }
            System.out.println(this.toString() + " activity's messaging system connected to the networking service.");
            runAfterConnectedToService();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            System.out.println(this.toString() + " activity's messaging system disconnected to the networking service.");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)   {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        alertDialogBuilder = new AlertDialog.Builder(this);
        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindToNetworkingService();
    }

    //Called by ServiceConnection after the connection to the service has been established
    protected abstract void runAfterConnectedToService();

    // Called by incomingHandler after receiving an ack with data from the service/server
    protected abstract void handleDataAck(ServiceMessages ackCode, Object rawData);

    // Called by incomingHandler after receiving an ack from the service/server
    protected abstract void handleAck(ServiceMessages ackCode);

    // Called by incomingHandler after receiving an offline ack, forbidding any server communication
    protected void handleOfflineAck()   {
        progressDialog.hide();
        showMessageDialog("Can't execute operation in OFFLINE mode.");
    }

    // Called by incomingHandler after receiving a timeout ack, terminating the connection
    protected void handleTimeoutAck()    {
        // Timeout login confirmation
        unbindToNetworkingService();
        progressDialog.hide();
        showMessageDialog("Couldn't connect to the server. Try again later.");
    }

    // Send a message to the service, with the intent of sending data: a constructed string for now.
    protected void redirectDataToServer(String data, ServiceMessages serviceMessages)   {
        try {
            Message msg = Message.obtain(null, serviceMessages.getMessageID());
            msg.obj = data;
            mService.send(msg);
        } catch (RemoteException e) {
            e.getStackTrace();
        }
    }

    protected void bindToNetworkingService()    {
        Intent intent = new Intent(this, NetworkingService.class);
        intent.putExtra(CONNECTION_HOST, host);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        boundToNetworkingService = true;
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
