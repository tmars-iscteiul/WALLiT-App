package com.example.wallit_app.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
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
    private boolean running;
    private boolean connected;

    public ServerConnectionHandler(NetworkingService nService, String host) {
        try {
            this.nService = nService;
            dataToSend = new LinkedList<>();
            running = true;
            serverSocket = new Socket();
            serverSocket.connect(new InetSocketAddress(host, 8080), 5000);  // Works only for this static port, while the host is provided by an input field from the user
            objectOut = new ObjectOutputStream(serverSocket.getOutputStream());
            objectIn  = new ObjectInputStream(serverSocket.getInputStream());
            connected = true;
            System.out.println("Connected to " + serverSocket.getInetAddress() + ":" + serverSocket.getPort());
        }   catch (IOException e) {
            connectionTimeout();
        }
    }

    /* Current problems with the connection thread
     * TODO: App doesn't fully close after prompt to close.
     * TODO: App doesn't fully terminate the connection with the server.
     * TODO: Lots of problems if the server is offline or can't be reached.
     */
    public synchronized void run()   {
        System.out.println("Connection handler started!");
        while(running) {
            if(!dataToSend.isEmpty() && connected)   {
                try {
                    String query = dataToSend.poll();
                    objectOut.writeObject(query);   // Crashes the app if the server is offline (nullpointerexception)
                    objectOut.reset();
                    System.out.println("Sent to server: \"" + query + "\".");
                    // TODO: Waiting forever on an ack. Set a timeout
                    try {
                        String ack = (String)objectIn.readObject();
                        System.out.println("Received from server: \"" + ack + "\".");
                        nService.returnAckToActivity(ack);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } catch (NullPointerException e) {
                    connectionTimeout();
                } catch (IOException e) {
                    connectionTimeout();
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
        nService.returnAckToActivity("MSG_CONNECTION_TIMEOUT");
    }

    // Called by the service, when an activity prompted it to send data to the server
    public synchronized void sendDataToServer(String data)   {
        dataToSend.add(data);
        notifyAll();
    }

    public boolean isConnected()   {
            return connected;
    }

    // Called when no activity is bound to the service (after user logout or app exit) which will close the socket to avoid memory and networking leaks
    // TODO This is being called if we minimize the application
    public synchronized void terminateConnection()   {
        try {
            running = false;
            objectOut.close();
            objectIn.close();
            serverSocket.close();
            System.out.println("Connection terminated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        notifyAll();
    }
}
