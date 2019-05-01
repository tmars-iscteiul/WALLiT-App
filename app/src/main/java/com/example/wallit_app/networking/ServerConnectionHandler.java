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
    private boolean receivedLastAck;

    public ServerConnectionHandler(NetworkingService nService, String host) {
        try {
            this.nService = nService;
            dataToSend = new LinkedList();
            running = true;
            receivedLastAck = false;
            serverSocket = new Socket(host, 8080);  // Works only for this static port, while the host is provided by an input field from the user
            System.out.println("Connected to " + serverSocket.getInetAddress() + ":" + serverSocket.getPort());
            objectOut = new ObjectOutputStream(serverSocket.getOutputStream());
            objectIn  = new ObjectInputStream(serverSocket.getInputStream());
        }   catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Current problems
     * TODO: App doesn't fully close after prompt to close.
     * TODO: App doesn't fully terminate the connection with the server.
     * TODO: App sends infinite IO exceptions to the server after being closed.
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
                        // TODO: Implement full message acks
                        if(ack.equals("POSITIVE_LOGIN_ACK"))  {
                            nService.returnAckToActivity(NetworkingService.MSG_ACK_POSITIVE);
                        }   else    {
                            nService.returnAckToActivity(NetworkingService.MSG_ACK_NEGATIVE);
                        }
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

    // TODO Could we mix logout and connection termination in one method/process/operation? If the user closes the connection (or it crashes) the server assumes it's a logout and the client forces-logout too?
    public void logoutUser(String username) {
        // TODO Add logout message to be sent to the server
        // For now, we're just closing the socket connection
        terminateConnection();
    }

    public synchronized void sendDataToServer(String data)   {
        dataToSend.add(data);
        notifyAll();
    }

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
