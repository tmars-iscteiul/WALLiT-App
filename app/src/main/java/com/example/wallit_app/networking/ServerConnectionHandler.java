package com.example.wallit_app.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    public ServerConnectionHandler(NetworkingService nService, String host) {
        try {
            this.nService = nService;
            dataToSend = new LinkedList();
            running = true;
            serverSocket = new Socket(host, 8080);  // Works only for this static port, while the host is provided by an input field from the user
            System.out.println("Connected to " + serverSocket.getInetAddress() + ":" + serverSocket.getPort());
            objectOut = new ObjectOutputStream(serverSocket.getOutputStream());
            objectIn  = new ObjectInputStream(serverSocket.getInputStream());
        }   catch (IOException e) {
            e.printStackTrace();
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
            if(!dataToSend.isEmpty())   {
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Called by the service, when an activity prompted it to send data to the server
    public synchronized void sendDataToServer(String data)   {
        dataToSend.add(data);
        notifyAll();
    }

    // Called when no activity is bound to the service (after user logout or app exit) which will close the socket to avoid memory and networking leaks
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
