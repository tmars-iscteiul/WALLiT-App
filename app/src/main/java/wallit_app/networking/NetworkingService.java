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

/**
 * This is the android {@link @Service} that will handle all the {@link ServerConnectionHandler}'s thread and communication between the handler and the bound activity.
 * @author skner
 */
public class NetworkingService extends Service {


    private ArrayList<Messenger> mClients = new ArrayList<>();

    private boolean offlineMode = false;
    private ServerConnectionHandler connectionHandler;
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    /**
     * Handles message sent from activities
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (ServiceMessages.getMessageByID(msg.what)) {
                case MSG_BIND:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNBIND:
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_LOGIN:
                    mClients.add(msg.replyTo);
                    if(!offlineMode) {
                        connectionHandler.sendDataToServer((String)msg.obj);
                        connectionHandler.start();
                    }   else    {
                        returnAckToActivity(new AckMessage(ServiceMessages.MSG_OFFLINE_ACK.getMessageString(), null));
                    }
                    break;
                case REQUEST_MOVEMENT_HISTORY:
                case REQUEST_FUND_INFO:
                case REQUEST_WITHDRAW:
                case REQUEST_DEPOSIT:
                case MSG_ACK_FUND_DATA:
                case MSG_ACK_USER_DATA:
                    handleMessageFromActivity((String)msg.obj);
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
            // TODO Remove this for final release
            if(connectionHandler.isConnected())
                Toast.makeText(this, "Disconnected from server.", Toast.LENGTH_SHORT).show();
            connectionHandler.terminateConnection();
        }
    }

    /**
     * Handles ack sent from the server, redirecting it to any bound activities (only one SHOULD BE bound at a time).
     * @param ackMessage {@link AckMessage} sent by the server.
     */
    public void returnAckToActivity(AckMessage ackMessage)    {
        Message msg = getMessageFromAck(ackMessage);
        for (int i = mClients.size()-1; i>=0; i--) {
            try {
                mClients.get(i).send(Message.obtain(msg));
            } catch (RemoteException e) {
                mClients.remove(i);
            }
        }
    }

    /**
     * Gateway to send information to the connection handler, or, if in offline-mode, generates an offline ack, letting the activity know it can't perform said operation
     * @param data Data sent by the activity
     */
    private void handleMessageFromActivity(String data) {
        if(!offlineMode) {
            connectionHandler.sendDataToServer(data);
        }   else    {
            returnAckToActivity(new AckMessage(ServiceMessages.MSG_OFFLINE_ACK.getMessageString(), null));
        }
    }

    /**
     * Converts a ackMessage's ackCode string into a Message object, by consulting the ServiceMessages enum, to be sent to the activity, annexing objects to it if necessary.
     * @param ackMessage The ack message previously received by the connection handler.
     * @return A complete message ready to be sent to the correspondent activity.
     */
    private Message getMessageFromAck(AckMessage ackMessage)    {
        int ackCode = ServiceMessages.getMessageByString(ackMessage.getAckMessageType()).getMessageID();
        Message msg = Message.obtain();
        msg.what = ackCode;
        if(ackCode == ServiceMessages.MSG_ACK_USER_DATA.getMessageID())
            msg.obj = ackMessage.getMovementEntryChunkList();
        if(ackCode == ServiceMessages.MSG_ACK_FUND_DATA.getMessageID())
            msg.obj = ackMessage.getFundInfoList();
        if(ackMessage.getLastRecordedBalance() != -1)
            msg.obj = ackMessage.getLastRecordedBalance();
        return msg;
    }

    public ServerConnectionHandler getConnectionHandler()   {
        return connectionHandler;
    }

    public void terminateConnection()   {
        stopSelf();
    }

}