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

    public ServerConnectionHandler(NetworkingService nService) {
        try {
            this.nService = nService;
            dataToSend = new LinkedList();
            running = true;
            receivedLastAck = false;
            serverSocket = new Socket("192.168.1.8", 8080);  // Works only for this static IPv4
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
     */
    public synchronized void run()   {
        System.out.println("Connection handler started!");
        while(running) {
            if(!dataToSend.isEmpty())   {
                try {
                    String query = dataToSend.poll();
                    objectOut.writeObject(query);   // Crashes the app if the server is offline (nullpointerexception)
                    objectOut.reset();
                    System.out.println("Sent: " + query);
                    // Wait for ack
                    // TODO: Waiting forever on ack. Set a timeout
                    try {
                        String ack = (String)objectIn.readObject();
                        System.out.println(ack);
                        if(ack.equals("Ack: " + query))  {
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

    public synchronized void sendDataToServer(String data)   {
        dataToSend.add(data);
        notifyAll();
    }

    public synchronized void terminateConnection()   {
        System.out.println("Terminating connection to server.");
        try {
            running = false;
            objectOut.close();
            objectIn.close();
            serverSocket.close();
            System.err.println("Connection terminated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        notifyAll();
    }
}
