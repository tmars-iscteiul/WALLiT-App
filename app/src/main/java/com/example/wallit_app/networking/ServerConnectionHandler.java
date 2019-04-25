package com.example.wallit_app.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ServerConnectionHandler extends Thread {

    private Socket serverSocket;
    private ObjectOutputStream objectOut;
    private ObjectInputStream objectIn;
    private Queue dataToSend;
    private boolean running;

    public ServerConnectionHandler() {
        try {
            dataToSend = new LinkedList();
            running = true;
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
        while(running) {
            if(!dataToSend.isEmpty())   {
                try {
                    System.out.println("Sending data string to java server.");
                    objectOut.writeObject(dataToSend.peek());
                    objectOut.reset();
                    System.out.println("Sent: " + dataToSend.poll());
                    // Wait for ack
                    try {
                        String ack = (String)objectIn.readObject();
                        System.out.println("Received ack from server: " + ack);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                System.out.println("Waiting for command...");
                wait();
                System.out.println("Command received!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            objectOut.close();
            objectIn.close();
            serverSocket.close();
            System.err.println("Connection terminated.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendDataToServer(String data)   {
        System.out.println("Trying to send \"" + data + "\" to server.");
        dataToSend.add(data);
        notifyAll();
    }

    public synchronized void terminateConnection()   {
        System.out.println("Terminating connection to server.");
        running = false;
        notifyAll();
    }
}
