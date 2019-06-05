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

import wallit_app.networking.NetworkingService;
import wallit_app.utilities.ServiceMessages;

/**
 * Binding activity is an abstract class activity that will serve as the base for any activity that binds to the {@link NetworkingService} service.
 * @author skner
 */
public abstract class BindingActivity extends AppCompatActivity {

    public static final String CONNECTION_HOST = "wallit_app.CONNECTIONHOST";
    public static final String LOGIN_USER = "wallit_app.LOGINUSER";
    public static final String USER_BALANCE = "wallit_app.USERBALANCE";

    protected Messenger mService = null;
    protected final Messenger mMessenger = new Messenger(new IncomingHandler());

    private boolean boundToNetworkingService = false;
    protected boolean loginOnBind = false;
    protected boolean userLoggedIn = false;
    protected String username = "default";
    protected String host = "192.168.1.8";
    protected double currentBalance = -1.0;

    protected AlertDialog.Builder alertDialogBuilder;
    protected AlertDialog alertDialog;
    protected ProgressDialog progressDialog;

    /**
     * Handles message sent from the service.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            System.out.println("Received data from server: " + msg.obj);
            handleAck(ServiceMessages.getMessageByID(msg.what), msg.obj);
        }
    }

    /**
     * Handles the service connection and binding operations.
     */
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

        // This is called when the connection with the service has been unexpectedly disconnected -- that is, its process crashed.
        public void onServiceDisconnected(ComponentName className) {
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

    /**
     * Called by ServiceConnection after the connection to the service has been established.
     */
    protected abstract void runAfterConnectedToService();

    /**
     * Called by incomingHandler after receiving an ack from the service/server.
     * @param ackCode ackCode received by the server, that will tell the activity how to handle the rawData object.
     * @param rawData An abstract object that, based on the ackCode received, will be converted to the correspondent data class, to be handled by the specific Activity.
     */
    protected abstract void handleAck(ServiceMessages ackCode, Object rawData);

    /**
     * Called by incomingHandler after receiving an offline ack, forbidding any server communication.
     */
    protected void handleOfflineAck()   {
        progressDialog.hide();
        showMessageDialog("Can't execute operation in OFFLINE mode.");
    }


    /**
     * Send a message to the service, with the intent of sending data: a constructed string for now.
     * @param data Data string to insert in the {@link Message} object, sent to the service.
     * @param serviceMessage {@link ServiceMessages} object that will be used to identify the message once it is sent to the service.
     */
    protected void redirectDataToServer(String data, ServiceMessages serviceMessage)   {
        try {
            Message msg = Message.obtain(null, serviceMessage.getMessageID());
            msg.obj = data;
            mService.send(msg);
        } catch (RemoteException e) {
            e.getStackTrace();
        }
    }

    /**
     * Called when the application starts the binding process.
     */
    protected void bindToNetworkingService()    {
        Intent intent = new Intent(this, NetworkingService.class);
        intent.putExtra(CONNECTION_HOST, host);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        boundToNetworkingService = true;
        System.out.println("Networking service intent bound to " + this.toString());
    }

    /**
     * Called when the application starts the unbinding process.
     */
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

    /**
     * Displays a message dialog with the input text.
     * @param text String to be displayed in the dialog message.
     */
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
