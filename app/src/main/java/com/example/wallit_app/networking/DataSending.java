package com.example.wallit_app.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class DataSending extends Thread {

    private String finalData;

    public DataSending(String text) {
        finalData = text;
    }

    public void run()   {
        try {
            sleep(5000);
            System.out.println("Trying to connect...");
            Socket socket = new Socket("192.168.1.8", 8080);  // Works only for this static IPv4
            System.out.println("Connected to " + socket.getInetAddress() + ":" + socket.getPort());
            System.out.println("Connected as " + socket.getLocalPort());
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

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
