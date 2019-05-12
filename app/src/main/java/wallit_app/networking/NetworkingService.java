package wallit_app.networking;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import wallit_app.activity.BindingActivity;
import wallit_app.data.AckMessage;
import wallit_app.utilities.ServiceMessages;

import java.util.ArrayList;

public class NetworkingService extends Service {


    private ArrayList<Messenger> mClients = new ArrayList<>();

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
                        connectionHandler.sendDataToServer("REQUEST_LOGIN,"+msg.obj);
                        connectionHandler.start();
                    }   else    {
                        returnAckToActivity(new AckMessage("MSG_OFFLINE_ACK", null));
                    }
                    break;
                case MSG_BIND:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNBIND:
                    mClients.remove(msg.replyTo);
                    break;
                case REQUEST_MOVEMENT_HISTORY:

                    break;
                case MSG_SEND_DATA:
                    if(!offlineMode) {
                        connectionHandler.sendDataToServer((String)msg.obj);
                    }   else    {
                        returnAckToActivity(new AckMessage("MSG_OFFLINE_ACK", null));
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
    public void returnAckToActivity(AckMessage ackMessage)    {
        Message msg = getMessageFromAck(ackMessage);
        for (int i = mClients.size()-1; i>=0; i--) {
            try {
                /* Sometimes it sends two messages (right after an activity has bound, without the previous one to unbind)
                 * TODO Try to unbind previous one right after next activity tries to bind
                 */
                mClients.get(i).send(Message.obtain(msg));
            } catch (RemoteException e) {
                mClients.remove(i);
            }
        }
    }

    // Consults the ServiceMessages enum to convert a ackMessage's ackCode string into a Message object, to be sent to the activity, annexing objects to it if necessary.
    private Message getMessageFromAck(AckMessage ackMessage)    {
        int ackCode = ServiceMessages.getMessageByString(ackMessage.getAckMessageType()).getMessageID();
        Message msg = Message.obtain();
        msg.what = ackCode;
        // TODO Add distinction between receiving FundInfo and UserStats by replacing MSG_SEND_DATA's message to two different ones
        // TODO Same message being used? What
        if(ackCode == ServiceMessages.MSG_SEND_DATA.getMessageID())   {
            msg.obj = ackMessage.getMovementEntryList();
        }
        return msg;
    }

    public ServerConnectionHandler getConnectionHandler()   {
        return connectionHandler;
    }

    public void terminateConnection()   {
        stopSelf();
    }

}