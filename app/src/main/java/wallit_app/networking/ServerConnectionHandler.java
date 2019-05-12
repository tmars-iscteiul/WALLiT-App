package wallit_app.networking;

import wallit_app.data.AckMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class ServerConnectionHandler extends Thread {

    private NetworkingService nService;
    private Socket serverSocket;
    private ObjectOutputStream objectOut;
    private ObjectInputStream objectIn;
    private Queue<String> dataToSend;
    private String host;
    private boolean running;
    private boolean connected;

    public ServerConnectionHandler(NetworkingService nService, String host) {
        this.nService = nService;
        this.host = host;
        dataToSend = new LinkedList<>();
        running = true;
        serverSocket = new Socket();
    }

    /* Current problems with the connection thread
     * TODO: App doesn't fully close after prompt to close.
     * TODO: App doesn't fully terminate the connection with the server.
     * TODO: Lots of problems if the server is offline or can't be reached.
     */
    public synchronized void run()   {
        try {
            serverSocket.connect(new InetSocketAddress(host, 4201), 5000);  // Works only for this static port, while the host is provided by an input field from the user
            objectOut = new ObjectOutputStream(serverSocket.getOutputStream());
            objectIn  = new ObjectInputStream(serverSocket.getInputStream());
        }   catch (IOException e)   {
            connectionTimeout();
            return;
        }
        connected = true;
        System.out.println("Connected to " + serverSocket.getInetAddress() + ":" + serverSocket.getPort());
        while(running) {
            if(!dataToSend.isEmpty() && connected)   {
                try {
                    String query = dataToSend.poll();
                    objectOut.writeObject(query);   // Crashes the app if the server is offline (nullpointerexception)
                    objectOut.reset();
                    System.out.println("Sent to server: \"" + query + "\".");
                    // TODO: Waiting forever on an ack. Set a timeout
                    try {
                        AckMessage ack = (AckMessage)objectIn.readObject();
                        System.out.println("Received from server: \"" + ack.getAckMessageType() + "\".");
                        nService.returnAckToActivity(ack);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } catch (NullPointerException e) {
                    connectionTimeout();
                    return;
                } catch (IOException e) {
                    connectionTimeout();
                    return;
                }
            }
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void connectionTimeout()    {
        System.out.println("Connection timed out.");
        nService.returnAckToActivity(new AckMessage("MSG_CONNECTION_TIMEOUT", null));
    }

    /*
     * Message standard sent by android client
     * Arg1: COMMAND
     * Arg2: USERNAME
     * Arg3: EXTRA DATA
     */
    // Called by the service, when an activity prompted it to send data to the server
    public synchronized void sendDataToServer(String data)   {
        dataToSend.add(data);
        notifyAll();
    }

    // Called when no activity is bound to the service (after user logout, app exit or any connection timeout/termination) which will close the socket to avoid memory and networking leaks
    // TODO This is being called if we minimize the application
    public synchronized void terminateConnection()   {
        try {

            if(objectOut != null)
                objectOut.close();
            if(objectIn != null)
                objectIn.close();
            if(serverSocket != null)
                serverSocket.close();
            System.out.println("Connection terminated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        running = false;
        connected = false;
        notifyAll();
    }

    public boolean isConnected()    {
        return connected;
    }
}
