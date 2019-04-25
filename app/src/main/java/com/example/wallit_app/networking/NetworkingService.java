package com.example.wallit_app.networking;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class NetworkingService extends Service {

    private ServerConnectionHandler connectionHandler;
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public NetworkingService getService() {
            return NetworkingService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        connectionHandler = new ServerConnectionHandler();
        connectionHandler.start();
        System.out.println("Connection handler started!");
        Toast.makeText(this, "networking service started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        connectionHandler.terminateConnection();
        Toast.makeText(this, "networking service done", Toast.LENGTH_SHORT).show();
    }

    public void sendDataToHandler(String data) {
        connectionHandler.sendDataToServer(data);
    }

    public void terminateConnection()   {
        stopSelf();
    }
}