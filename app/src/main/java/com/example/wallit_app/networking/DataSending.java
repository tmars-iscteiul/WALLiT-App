package com.example.wallit_app.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class DataSending extends Thread {

    private String finalData;

    public DataSending(String text) {
        finalData = text;
    }

    // Very rough, slow and bad. Terrible in fact. TODO change this later...
    // Every click, a new connection is being established without the last one being closed. TODO Fix it by stablishing a connection when user logs in.
    public void run()   {
        try {
            Socket socket = new Socket("192.168.1.8", 8080);  // Works only for this static IPv4
            System.out.println("Connected to " + socket.getInetAddress() + ":" + socket.getPort());
            ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectIn  = new ObjectInputStream(socket.getInputStream());

            System.out.println("Sending data string to java server.");
            objectOut.writeObject(finalData);
            objectOut.reset();
            System.out.println("Sent: " + finalData);

            // Wait for ack
            try {
                System.out.println("Received this from server:");
                System.out.println(objectIn.readObject());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
